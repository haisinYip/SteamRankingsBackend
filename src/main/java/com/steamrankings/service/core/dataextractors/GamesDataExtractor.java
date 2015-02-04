package com.steamrankings.service.core.dataextractors;

import com.github.koraktor.steamcondenser.steam.community.WebApi;

public class GamesDataExtractor {
	
	private String APIKEY = "3A7D85F3F85FE936F9573F9BDF559089";
	//final private static int STEAM_PROFILE_VISIBILITY_PUBLIC = 3;
	String id64;	
	
	public GamesDataExtractor(String id64) {
		this.id64 = id64;
	}
	
	public void addGames() {
		
		
		WebApi.setApiKey(APIKEY);
		
		String jsonString = WebApi.getJSON("IPlayerService", "GetOwneGames", 1, params)
	}

}
