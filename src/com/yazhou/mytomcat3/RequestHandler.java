package com.yazhou.mytomcat3;


import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;

public class RequestHandler implements Runnable{
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


    private SelectionKey key;


    public RequestHandler(SelectionKey key){
        this.key=key;
    }

    @Override
    public void run() {
        try {
            SocketChannel channel = (SocketChannel)key.channel();


            Request request = new Request(channel);
             url=request.getUrl();

//            System.out.println("url   "+url);
            //判断本次请求是静态demo.html页面还是运行在服务端的一段java小程序 demo1.html aa
            if (null!=url){
                if (url.indexOf(".")!=-1){

                    //发送静态资源
                    sendStaticResource(channel);
                }else
                    sendDynamicResource(channel);
            }

            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    private  void sendDynamicResource( SocketChannel channel) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        //将http协议的响应头发送到客户端
        String ok="HTTP/1.1 200 OK\n"+"Server:apache\n"+"Content-Type:text/html;charset=utf-8\n"+"\n";
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.clear();
        buffer.put(ok.getBytes());
        buffer.flip();
        channel.write(buffer);

        buffer.clear();
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
            servlet.service(buffer);
            buffer.flip();
            channel.write(buffer);
        }

    }

    //发送静态资源
    private  void sendStaticResource(SocketChannel channel) throws IOException {



        ByteBuffer buffer = ByteBuffer.allocate(1024);

        //定义一个文件输入流，用户获取静态资源demo01.html的内容
        FileInputStream fis=null;
        try {
            File file = new File(WEB_ROOT, url);
            if (file.exists()){

                String ok="HTTP/1.1 200 OK\n"+"Content-Type:text/html;charset=utf-8\n"+"\n";

                buffer.clear();
                buffer.put(ok.getBytes());

                //获取到文件输入流对象
                fis=new FileInputStream(file);
                int n=0;

                while (n!=-1)  //当n不等于-1,则代表未到末尾
                {

                    buffer.put((byte) n);
                    n=fis.read();//读取文件的一个字节(8个二进制位),并将其由二进制转成十进制的整数返回


                }


                buffer.flip();
                channel.write(buffer);
            }else {
                //如果文件不存在
                //向客户端响应文件不存在消息
                String ok="HTTP/1.1 404 Not Found\n"+"Server:apache-Coyote"+"Content-Type:text/html;charset=utf-8\n"+"\n"+"file not find";

                buffer.clear();
                buffer.put(ok.getBytes());
                buffer.flip();
                channel.write(buffer);

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
}
