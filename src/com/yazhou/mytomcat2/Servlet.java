package com.yazhou.mytomcat2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//所有服务端的java小城内需要实现的接口
public interface Servlet {
    //初始化
    public void init();

    //服务
    public void service(InputStream is, OutputStream ops) throws IOException;

    //销毁
    public void destroy();
}
