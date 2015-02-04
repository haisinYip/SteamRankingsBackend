package com.steamrankings.service.core.dataextractors;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.steam.community.SteamId;
import com.github.koraktor.steamcondenser.steam.community.WebApi;
import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.steamapi.SteamrollerApi;

public class ProfileDataExtractor extends SteamProfile{

	Long id64;
	String communityID;
	String personName;
	String realName;
	String country;
	String lastonline;
	DateTime time;
	String totalPlayTime;
	String avatar;
	String apikey = "XXXXX";

	public ProfileDataExtractor(Long id64, String communityID, String personaName) {
		super(id64, communityID, personaName);
	}

	public SteamProfile profile(Long id64) throws Exception {
		
		
		SteamProfile sp = new SteamProfile(id64, communityID, personName);
		WebApi.setApiKey(apikey);
		
		SteamId steamId = SteamId.create(id64, true);
		String steamid = steamId.getRealName();
		
		System.out.println(steamid);
		
		//Map<String, Object> param = new HashMap<String, Object>();
		//param.put("steamids", id64);
		
		//SteamrollerApi api = new SteamrollerApi(apikey); 
		/*String jsonString = api.getJSON("ISteamUSer", "GetPlayerSummaries", 2, param);
		System.out.println(jsonString);

		JSONObject json = new JSONObject(jsonString);
		System.out.println(json);

		json = (JSONObject) json.get("response");
		System.out.println(json);

		JSONArray jsonArray = json.getJSONArray("players");
		System.out.println(jsonArray);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonElement = jsonArray.getJSONObject(i);
			Iterator itr = jsonElement.keys();
			while(itr.hasNext()) {
				String element = (String) itr.next();
				if (element.equals("steamid")) {	
					id64 = jsonElement.getString("steamid");
					System.out.println(id64);
					sp.setSteamId64(id64);
				} else if(element.equals("personaname")) {
					personName = jsonElement.getString("personaname");
					System.out.println(personName);
					sp.setPersonaName(personName);
				} else if(element.equals("lastlogoff")) {
					lastonline = jsonElement.getString("lastlogoff");
					int temp = Integer.parseInt(lastonline);
					//temp = temp * 1000;
					String temp2 = Integer.toString(temp);
					//time = new DateTime(temp2);
					System.out.println(temp2);
					sp.setLastOnlineTime(temp2);
				} else if(element.equals("profileurl")) {
					communityID = getComID(jsonElement.getString("profileurl"));	//parse string to get end part of url containing profileName
					System.out.println(communityID);
					sp.setSteamCommunityId(communityID);
				} else if(element.equals("avatar")) {
					avatar = jsonElement.getString("avatar");;
					System.out.println(avatar);
					sp.setAvatar(avatar);
				} else if(element.equals("realname")) {
					realName = jsonElement.getString("realname");
					System.out.println(realName);
					sp.setRealName(realName);
				} else if(element.equals("loccountrycode")) {
					country = jsonElement.getString("loccountrycode");
					System.out.println(country);
					sp.setCountryCode(country);
				} 
			}
		}
		//go through gamesOwnedList and find playtime_forever field(in minutes) for each game and sum
		//***Need to change int of minutes to time still***
		totalPlayTime = totalPlayTime(id64);	
		System.out.println(totalPlayTime + " HERE");
		sp.setTolalPlayTime(totalPlayTime);*/
		
		return sp;
	}
	//method for getting playtime forever of each game in IPlayerService
	public String totalPlayTime(String id64) throws Exception{
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("steamid", id64);
		
		SteamrollerApi api = new SteamrollerApi(apikey);
		String jsonString = api.getJSON("IPlayerService", "GetOwnedGames", 1, param);
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
					totalPlayTime = jsonGameElement.getString("playtime_forever");
					//System.out.println(totalPlayTime);
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
		profileName = communityID.substring(i + 1, communityID.length() - 1);
		//System.out.println(profileName);
		return profileName;
	}
}