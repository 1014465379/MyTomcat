package com.yazhou.mytomcat4;

import com.yazhou.mytomcat3.Request;
//import com.yazhou.mytomcat3.RequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * NIO版本的tomcat
 * 监听请求,调用request和response对请求作出反应
 * @author 申劭明
 * @date 2020/4/18 17:21
 * @version 5.1
 */

public class HttpServer {

    /**
     * 监听端口
     */
    private static int port = 9000;

    /**
     * Key值为Servlet的别名(uri),value为该Servlet对象
     * default权限
     */

    /**
     * 监听通道
     */
    private ServerSocketChannel serverSocketChannel;
    /**
     * NIO负责轮询的Selector
     */
    private Selector selector;

    /**
     * @Description : nio监听数据请求
     * @author : 申劭明
     * @date : 2019/9/17 10:29
     */
    public void acceptWait() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            // 设置ServerSocketChannel为非阻塞
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(9000));
            //selector获取不同操作系统下不同的TCP连接动态
            selector = Selector.open();

            //给当前的serverSocketChannel注册选择器，根据条件查询符合情况的TCP连接
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                selector.select()  ;
                // 获取当前时间点的所有事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                // 遍历所有有事件发生的通道
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    // 判断当前通道是否已经做好socket连接的准备
                    if (key.isAcceptable()) {
                        //拿到新的对象

                        SocketChannel channel = serverSocketChannel.accept();
                        if (channel != null) {
                            // 注册连接对象，进行关注，no-Blocking
                            channel.configureBlocking(false);
                            // 将该 socketChannel 通道注册到selector中
                            // 注意,上面的注册是ServerSocketChannel

                            channel.register(selector, SelectionKey.OP_READ);
                        }
                    } else if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        SocketChannel channel = (SocketChannel)key.channel();
                        //我们把通道的数据填入缓冲区
                        channel.read(buffer);
                        String request = new String(buffer.array()).trim();
//                        new RequestHandler(channel.socket());
                        System.out.println("客户端的请求内容" + request);
                        //把我们的html内容返回给客户端
                        String outString = "HTTP/1.1 200 OK\n"
                                +"Content-Type:text/html; charset=UTF-8\n\n"
                                +"<html>\n"
                                +"<head>\n"
                                +"<title>first page</title>\n"
                                +"</head>\n"
                                +"<body>\n"
                                +"hello fomcat\n"
                                +"</body>\n"
                                +"</html>";

                        ByteBuffer outbuffer = ByteBuffer.wrap(outString.getBytes());
                        channel.write(outbuffer);
                        channel.close();

                    }
                }
                // 检查过程就绪,清除之前的调用效果
                selector.selectNow();
            } catch (IOException e) {
                // 避免因为某一个请求异常而导致程序终止
                e.printStackTrace();
            }

        }
    }

}

