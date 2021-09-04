package com.yazhou.mytomcat2;

import jdk.nashorn.internal.ir.RuntimeNode;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class TestClient1 {
    //定义变量，存放WebContent目录的绝对路径
    public  static String WEB_ROOT=System.getProperty("user.dir")+"\\"+"WebContent";

    //定义静态变量，存放本次请求的静态页面名称目录
    public  static String url="";

    //定义一个静态类型的map，存储服务端conf.properties中的配置信息
    private static Map<String,String> map=new HashMap<>();

    static {
        //服务器启动之前将配置参数中的信息加载到Map中
        //创建一个Properties对象
        Properties prop=new Properties();
        //加载WebContent目录下的conf.properties文件
        try {
            prop.load(new FileInputStream(WEB_ROOT+"\\conf.properties"));

            //将配置文件中的数据流读取到map
            Set set = prop.keySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()){
                String key=(String) iterator.next();
                String value=prop.getProperty(key);
                map.put(key,value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        //System.out.println(WEB_ROOT); 测试路径是否正确


//无nio
        //创建serversocker，监听本机80端口，等待来自客户端的请求
        ServerSocket serverSocket=null;
        Socket socket=null;
        InputStream is=null;
        OutputStream ops=null;
        try {
            serverSocket = new ServerSocket(8081);
            while(true) {
                //获取到客户端对应的socket
                socket =  serverSocket.accept();


                //获取输入流对象

                is=socket.getInputStream();
                //获取输出流对象

                ops=socket.getOutputStream();
                //获取HTTP协议的请求部分，截取客户端要访问的资源名称，将这个资源名称赋值给url

                parset(is);
                //判断本次请求是静态demo.html页面还是运行在服务端的一段java小程序 demo1.html aa
                if (null!=url){
                    if (url.indexOf(".")!=-1){

                        //发送静态资源
                        sendStaticResource(ops);
                    }else
                        sendDynamicResource(is,ops);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            if (null != is) {
                is.close();
                is = null;
            }
            if (null != ops) {
                ops.close();
                ;
                ops = null;

            }
            if (null != socket) {
                socket.close();
                socket = null;
            }


        }}

    private static void sendDynamicResource(InputStream is, OutputStream ops) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        //将http协议的响应头发送到客户端
        ops.write("HTTP/1.1 200 OK\n".getBytes());
        ops.write("Server:apache\n".getBytes());
        ops.write("Content-Type:text/html;charset=utf-8\n".getBytes());
        ops.write("\n".getBytes());

        //判断map是否存在一个key，这个key是否和本次请求的资源路径一致
        if (map.containsKey(url)){

            //如果包括指定的key,获取到value
            String value=map.get(url);

            //通过反射将java程序加载到内存中
            Class clazz=Class.forName(value);

            Servlet servlet=(Servlet) clazz.newInstance();

            //执行init方法
            servlet.init();

            //执行service方法
            servlet.service(is,ops);
        }

    }

    //发送静态资源
    private static void sendStaticResource(OutputStream ops) throws IOException {

        //定义一个字节数组，用于存放本次请求的静态资源demo01.html

        byte[] bytes=new byte[2048];

        //定义一个文件输入流，用户获取静态资源demo01.html的内容
        FileInputStream fis=null;
        try {
            File file = new File(WEB_ROOT, url);
            if (file.exists()){

                //向客户端输出http协议的响应头

                ops.write("HTTP/1.1 200 OK\n".getBytes());
                ops.write("Server:apache-Coyote\n".getBytes());
                ops.write("Content-Type:text/html;charset=utf-8\n".getBytes());
                ops.write("\n".getBytes());

                //获取到文件输入流对象
                fis=new FileInputStream(file);
                //读取静态资源demo01.html的内容到数组中
                int ch=fis.read(bytes);
                while (ch!=-1){
                    //将读取到数组内的内容通过输出流发送到客户端
                    ops.write(bytes,0,ch);
                    ch=fis.read(bytes   );
                }
            }else {
                //如果文件不存在
                //向客户端响应文件不存在消息
                ops.write("HTTP/1.1 404 Not Found\n".getBytes());
                ops.write("Server:apache-Coyote".getBytes());
                ops.write("Content-Type:text/html;charset=utf-8\n".getBytes());
                ops.write("\n".getBytes());
                String ERROR_MESSAGE="file not find";
                ops.write(ERROR_MESSAGE.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //释放文件输入流
            if (null!=fis){
                fis.close();
                fis=null;
            }
        }

    }

    //获取HTTP协议的请求部分，截取客户端要访问的资源名称，将这个资源名称赋值给url

    private static void parset(InputStream is) throws IOException {

        //定义一个变量，存放http协议请求部分数据

        StringBuffer content=new StringBuffer(2048);

        //定义一个数组，存放http协议请求部分数据

        byte[] buffer=new byte[2048];

        //定义一个变量i，代表数据到数组之后，数据量的大下

        int i=-1;

        //读取客户端发送过来的数据，将数据读取到字节数组buffer.i代表数据量的大小

        i=is.read(buffer);

        //遍历字节数组，将数组中的数据追加到content变量中

        for(int j=0;j<i;j++){

            content.append((char)(buffer[j]));
        }

        //打印http协议请求部分数据

        System.out.println(content);

        //截取客户端邀请去的资源路径demo html辅助给url

        parseUrl(content);
    }
    //截取客户端邀请去的资源路径demo.html辅助给url
    private static void parseUrl(StringBuffer content) {

        //定义2个变量，存放请求行的2个空格的位置

        int index1,index2;

        //获取http请求部分第一个空格的位置

        index1=content.indexOf(" ");
        if(index1!=-1){
            //获取http请求部分第二个空格的位置

            index2=content.indexOf(" ",index1+1);

            if(index2>index1){

                //截取字符串获取到本次请求
                url=content.substring(index1+2,index2);
            }
        }

        //打印本次请求静态资源的名称
        System.out.println(url);



    }
}
