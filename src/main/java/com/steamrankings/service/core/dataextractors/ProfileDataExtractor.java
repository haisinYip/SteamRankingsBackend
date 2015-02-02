package com.steamrankings.service.core.dataextractors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.steam.community.WebApi;
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
	String apikey = "XXXXX";

	public ProfileDataExtractor(String id64, String communityID, String personaName) {
		super(id64, communityID, personaName);
	}

	public SteamProfile profile(String id64) throws Exception {
		
		SteamProfile sp = new SteamProfile(id64, communityID, personName);
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("steamids", id64);
		
		SteamrollerApi api = new SteamrollerApi(apikey); 
		String jsonString = WebApi.getJSON("ISteamUSer", "GetPlayerSummaries", 2, param);
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
					sp.setSteamId64(id64);
				} else if(element.equals("personaname")) {
					personName = element;
					System.out.println(personName);
					sp.setPersonaName(personName);
				} else if(element.equals("realname")) {
					realName = element;
					System.out.println(realName);
					sp.setRealName(realName);
				} else if(element.equals("profileurl")) {
					System.out.println(element);
					communityID = getComID(element);	//parse string to get end part of url containing profileName
					System.out.println(communityID);
					sp.setSteamCommunityId(communityID);
				} else if(element.equals("loccountrycode")) {
					country = element;
					System.out.println(country);
					sp.setCountryCode(country);
				} else if(element.equals("lastlogoff")) {
					lastonline = element;
					time = new DateTime(lastonline);
					System.out.println(time);
					sp.setLastOnlineTime(time);
				} else if(element.equals("avatar")) {
					avatar = element;
					System.out.println(avatar);
					sp.setAvatar(avatar);
				}
			}
		}
		//go through gamesOwnedList and find playtime_forever field(in minutes) for each game and sum
		//***Need to change int of minutes to time or string still***
		totalPlayTime = totalPlayTime(api);	
		sp.setTolalPlayTime(totalPlayTime);
		
		return sp;
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