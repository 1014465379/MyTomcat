package com.yazhou.mytomcat3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

//所有服务端的java小城内需要实现的接口
public interface Servlet {
    //初始化
    public void init();

    //服务
    public void service(ByteBuffer buffer) throws IOException;

    //销毁
    public void destroy();
}
