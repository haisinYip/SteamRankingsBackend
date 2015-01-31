package com.steamrankings.service.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Application {
	public static void main(String[] args) throws IOException {
		int port = 6789;

		ServerSocket serverSocket;
		serverSocket = new ServerSocket(port);

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