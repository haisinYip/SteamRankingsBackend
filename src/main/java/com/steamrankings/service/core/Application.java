package com.steamrankings.service.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.WebApi;

public class Application {
    public static void main(String[] args) throws IOException {
        int port = 6789;

        ServerSocket serverSocket;
        serverSocket = new ServerSocket(port);

        try {
            WebApi.setApiKey("3A7D85F3F85FE936F9573F9BDF559089");
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