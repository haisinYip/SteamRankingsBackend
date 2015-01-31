package com.steamrankings.steamapi;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.WebApi;


public class SteamrollerApi extends WebApi {
	public SteamrollerApi(String apiKey) throws WebApiException {
		this.setApiKey(apiKey);
	}
}