package com.steamrankings.service.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.WebApi;

public class Application {
    final public static Properties CONFIG = new Properties();
    
    public static void main(String[] args) throws IOException {
        InputStream inputStream = new FileInputStream("config.properties");
        CONFIG.load(inputStream);
        inputStream.close();
        
        int port = Integer.parseInt(CONFIG.get("server_port").toString());

        ServerSocket serverSocket;
        serverSocket = new ServerSocket(port);

        try {
            WebApi.setApiKey(CONFIG.getProperty("apikey"));
        } catch (WebApiException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        while (true) {
            Socket clientSocket = serverSocket.accept();
            RequestHandler requestHandler;

            try {
                requestHandler = new RequestHandler(clientSocket);
            } catch (Exception e) {
                break;
            }
            new Thread(requestHandler).start();
        }

        serverSocket.close();
    }
}