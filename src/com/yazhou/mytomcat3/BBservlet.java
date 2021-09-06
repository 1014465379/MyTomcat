package com.yazhou.mytomcat3;

import com.yazhou.mytomcat3.Servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class BBservlet implements Servlet {
    @Override
    public void init() {
        System.out.println("bbServlet___init");
    }

    @Override
    public void service(ByteBuffer buffer) throws IOException {
        System.out.println("bbServlet___service");
        buffer.put("I am from bbServlet".getBytes());
    }

    @Override
    public void destroy() {

    }
}
