package com.yazhou.mytomcat3;

import java.io.*;

public class TestClient {


    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        HttpServer httpServer = new HttpServer();
        httpServer.serve();
    }

}
