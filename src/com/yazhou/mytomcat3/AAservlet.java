package com.yazhou.mytomcat3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class AAservlet implements Servlet {
    @Override
    public void init() {
        System.out.println("aaServlet___init");
    }

    @Override
    public void service( ByteBuffer buffer) throws IOException {
        System.out.println("aaServlet___service");
        buffer.put("I am from aaServlet".getBytes());
    }

    @Override
    public void destroy() {

    }
}
