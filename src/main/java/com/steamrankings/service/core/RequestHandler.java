package com.steamrankings.service.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
	private Socket socket;
	private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());

	public RequestHandler(Socket socket) throws Exception {
		this.socket = socket;
	}

	public void run() {
		try {
			processRequest();
		} catch (IOException e) {
			logger.log(Level.FINE, "Error processing the request." + e);
		}
	}
	
	private void processRequest() throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		String requestLine = input.readLine();

		System.out.println();
		System.out.println(requestLine);

		String headerLine;
		do {
			headerLine = input.readLine();
			System.out.println(headerLine);
		} while (!headerLine.equals(""));

		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();
		String fileName = tokens.nextToken();
		String[] request = fileName.split("\\?");
		String name = "Not found";
		if (request[0].equals("/profile")) {
			System.out.println(fileName);
		} else {
			System.out.println(name);
		}
	}
}
