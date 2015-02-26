package com.steamrankings.service.api;

import org.apache.http.client.HttpClient;

public class SteamRankingsClient {
	private HttpClient client;
	private String environment;
	
	public SteamRankingsClient(String environment) {
		// HTTP client
	}
	
	public String execute(String request) {
		return null;
	}
	
	public String getServerUrl() {
		// return the URL eg localhost:6789 or mikemontreal.ignorelist.com:6789
		return null;
	}
}