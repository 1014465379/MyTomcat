package com.yazhou.mytomcat4;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class RequestHandler {

    /**
     * @Description : nio中的消息处理
     *
     * @param socketChannel socket通道对象,相当于一次socket连接
     * @param buffer 读取到的请求内容
     * @Return : void
     * @Author : 申劭明
     * @Date : 2020/4/17 15:48
     */
    static void handler(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        // 时间关系就不接之前的Servlet处理逻辑啦
        System.out.println("Receive message: " + new String(buffer.array()));
        String resultData = "HelloWorld";
        socketChannel.write(ByteBuffer.wrap(resultData.getBytes()));

    }
}

