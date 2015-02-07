package com.steamrankings.service.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Application {
    final public static Properties CONFIG = new Properties();

    public static void main(String[] args) throws IOException {
        InputStream inputStream = new FileInputStream("config.properties");
        CONFIG.load(inputStream);
        inputStream.close();

        int port = Integer.parseInt(CONFIG.get("server_port").toString());

        ServerSocket serverSocket;
        serverSocket = new ServerSocket(port);
        System.out.println("Backend now running");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            RequestHandler requestHandler;

            try {
            	System.out.println("New request received");
                requestHandler = new RequestHandler(clientSocket);
                
            } catch (Exception e) {
                break;
            }
            new Thread(requestHandler).start();
        }

        serverSocket.close();
    }
}