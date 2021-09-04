package com.yazhou.mytomcat2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BBservlet implements Servlet{
    @Override
    public void init() {
        System.out.println("bbServlet___init");
    }

    @Override
    public void service(InputStream is, OutputStream ops) throws IOException {
        System.out.println("bbServlet___service");
        ops.write("I am from bbServlet".getBytes());
        ops.flush();
    }

    @Override
    public void destroy() {

    }
}
