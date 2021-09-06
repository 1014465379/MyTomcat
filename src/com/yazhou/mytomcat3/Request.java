package com.yazhou.mytomcat3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Request {


    private String url;

    public Request(SocketChannel channel) throws IOException {
        parset(channel);
    }
    public String getUrl(){
        return this.url;
    }

    private  void parset(SocketChannel channel) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //我们把通道的数据填入缓冲区
        channel.read(buffer);

        String a = new String(buffer.array()).trim();
        StringBuffer request=new StringBuffer(a);

        System.out.println(request);

        //截取客户端邀请去的资源路径demo html辅助给url

        parseUrl(request);
    }
    //截取客户端邀请去的资源路径demo.html辅助给url
    private  void parseUrl(StringBuffer content) {

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




    }
}
