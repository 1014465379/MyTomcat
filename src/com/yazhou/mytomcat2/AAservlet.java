package com.yazhou.mytomcat2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AAservlet implements Servlet{
    @Override
    public void init() {
        System.out.println("aaServlet___init");
    }

    @Override
    public void service(InputStream is, OutputStream ops) throws IOException {
        System.out.println("aaServlet___service");
        ops.write("I am from aaServlet".getBytes());
        ops.flush();
    }

    @Override
    public void destroy() {

    }
}
