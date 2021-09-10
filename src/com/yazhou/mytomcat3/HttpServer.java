package com.yazhou.mytomcat3;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class HttpServer {
    private ServerSocketChannel serverSocketChannel ;//监听通道

    private  Selector selector;//NIO负责轮询的selector

    public void serve() {
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
                selector.select();
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

                        RequestHandler requestHandler = new RequestHandler(key);

                        requestHandler.run();
                        //把我们的html内容返回给客户端

                    }
                }
                // 检查过程就绪,清除之前的调用效果
                selector.selectNow();
            } catch (IOException e) {
                // 避免因为某一个请求异常而导致程序终止
                e.printStackTrace();
            }

        }
    }}
