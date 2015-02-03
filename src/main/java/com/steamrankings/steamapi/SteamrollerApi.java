package com.steamrankings.steamapi;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.WebApi;

public class SteamrollerApi extends WebApi {
	public SteamrollerApi(String apiKey) throws WebApiException {
		//String apikey = "B14A5DC1B77DC531F881389B045B8495";
		this.setApiKey(apiKey);
	}
}