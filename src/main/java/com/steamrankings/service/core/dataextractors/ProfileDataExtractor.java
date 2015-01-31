package com.steamrankings.service.core.dataextractors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import com.steamrankings.service.api.games.SteamGame;
import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.steamapi.SteamrollerApi;

public class ProfileDataExtractor extends SteamProfile{

	String id64;
	String communityID;
	String personName;
	String realName;
	String country;
	String lastonline;
	DateTime time;
	String totalPlayTime;
	String avatar;
	String apikey = "B14A5DC1B77DC531F881389B045B8495";

	public ProfileDataExtractor(String id64, String communityID, String personaName, String realName,
				String country, String lastonline, DateTime time, String totalPlayTime, String avatar) {
		super(id64, communityID, personaName);
		this.realName = realName;
		this.country = country;
		this.lastonline = lastonline;
		this.time = time;
		this.totalPlayTime = totalPlayTime;
		this.avatar = avatar;
	}

	public SteamProfile profile() throws Exception {

		ProfileDataExtractor pde = new ProfileDataExtractor(id64, communityID, personName, realName, country, 
															lastonline, time, totalPlayTime, avatar);

		SteamrollerApi api = new SteamrollerApi(apikey); 
		String jsonString = api.getJSON("ISteamUSer", "GetPlayerSummaries", 2);
		JSONObject json = new JSONObject(jsonString);

		json = (JSONObject) json.get("response");

		JSONArray jsonArray = json.getJSONArray("players");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonElement = jsonArray.getJSONObject(i);
			Iterator itr = jsonElement.keys();
			while(itr.hasNext()) {
				String element = (String) itr.next();
				if (element.equals("steamid")) {	
					id64 = element;
					System.out.println(id64);
					super.setSteamId64(id64);
				} else if(element.equals("personaname")) {
					personName = element;
					System.out.println(personName);
					super.setPersonaName(personName);
				} else if(element.equals("realname")) {
					realName = element;
					System.out.println(realName);
					super.setRealName(realName);
				} else if(element.equals("profileurl")) {
					System.out.println(element);
					communityID = getComID(element);	//parse string to get end part of url containing profileName
					System.out.println(communityID);
					super.setSteamCommunityId(communityID);
				} else if(element.equals("loccountrycode")) {
					country = element;
					System.out.println(country);
					super.setCountryCode(country);
				} else if(element.equals("lastlogoff")) {
					lastonline = element;
					time = new DateTime(lastonline);
					System.out.println(time);
					super.setLastOnlineTime(time);
				} else if(element.equals("avatar")) {
					avatar = element;
					System.out.println(avatar);
					super.setAvatar(avatar);
				}
			}
		}
		//go through gamesOwnedList and find playtime_forever field(in minutes) for each game and sum
		//***Need to change int of minutes to time or string still***
		totalPlayTime = totalPlayTime(api);	
		super.setTolalPlayTime(totalPlayTime);
		
		return pde;
	}
	//method for getting playtime forever of each game in IPlayerService
	public String totalPlayTime(SteamrollerApi api) throws Exception{
		
		String jsonString = api.getJSON("IPlayerService", "GetOwnedGames", 1);
		JSONObject json = new JSONObject(jsonString);
		int count = 0;			//holds total count of minutes for every game owned
		String totalCount;
		
		json = (JSONObject) json.get("response");
		JSONArray jsonArrayGames = json.getJSONArray("games");
		System.out.println(jsonArrayGames);

		for (int i = 0; i < jsonArrayGames.length(); i++) {
			JSONObject jsonGameElement = jsonArrayGames.getJSONObject(i);
			Iterator itr = jsonGameElement.keys();
			while(itr.hasNext()) {
				String element = (String) itr.next();
				if (element.equals("playtime_forever")) {
					totalPlayTime = element;
					System.out.println(totalPlayTime);
					int temp = Integer.parseInt(totalPlayTime);
					count += temp;
				}	
			}
		}
		totalCount = Integer.toString(count);
		return totalCount;
	}
	
	//method that is passed profileurl and extracts name from it(at the end of the url)
	//after the fourth instance of '\' the profilename starts
	public String getComID(String communityID) {
		String profileName;
		int i;
		int count = 0;		//counter variable for number of '/'
		for(i = 0; i < communityID.length(); i++) {
			if(communityID.charAt(i) == '/') {	// '/' detected increment
				count++;
			}
			if(count == 4) {		//4 '/' instances; next char till length - 1 is the profileName
				break;
			}
		}
		profileName = communityID.substring(i + 1, communityID.length());
		System.out.println(profileName);
		return profileName;
	}
}