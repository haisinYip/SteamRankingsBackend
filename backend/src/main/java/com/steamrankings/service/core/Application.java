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

		// Load configuration file
		InputStream inputStream = new FileInputStream("config.properties");
		CONFIG.load(inputStream);
		inputStream.close();

		// Open socket to receive requests from frontend
		ServerSocket serverSocket = new ServerSocket(Integer.parseInt(CONFIG
				.get("server_port").toString()));
		System.out.println("Backend now running");
		
		while (true) {
			// Receive requests and handle them
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