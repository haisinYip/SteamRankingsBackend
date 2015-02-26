package com.steamrankings.service.api.achievements;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;

/*
public class Achievements {
	
		private static final String API_ERROR_BAD_ARGUMENTS_CODE = "1000";
	    private static final String API_ERROR_STEAM_USER_DOES_NOT_EXIST = "2000";
	    private static final String API_ERROR_STEAM_ID_INVALID = "3000";
	    
	    
    public static List<GameAchievement> getGameAchievements(int appId) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:6789/achievements?appid=" + appId);
        HttpResponse response = null;
*/
import com.steamrankings.service.api.client.SteamRankingsClient;

public class Achievements {
    public static List<GameAchievement> getGameAchievements(int appId, SteamRankingsClient client) {
        try {
        	String data=client.excecuteRequest("achievements?appid=" + appId);

            ObjectMapper mapper = new ObjectMapper();
            AchievementWrapper achievements = mapper.readValue(data, AchievementWrapper.class);

            return achievements.getAchievements();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<GameAchievement> getUnlockedAchievements(String steamID64, SteamRankingsClient client) {

        try {
            String data = client.excecuteRequest("achievements?id=" + steamID64);

            ObjectMapper mapper = new ObjectMapper();
            JSONArray jsonArray = new JSONArray(data);
            ArrayList<GameAchievement> achievements = new ArrayList<GameAchievement>();

            for (int i = 0; i < jsonArray.length(); i++) {
                achievements.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), GameAchievement.class));
            }

            return achievements;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<GameAchievement> getUnlockedAchievements(String steamID64, int appId, SteamRankingsClient client ) {

        try {
            String data = client.excecuteRequest("achievements?id=" + steamID64+"&appid=" + appId);

            ObjectMapper mapper = new ObjectMapper();
            JSONArray jsonArray = new JSONArray(data);
            ArrayList<GameAchievement> achievements = new ArrayList<GameAchievement>();

            for (int i = 0; i < jsonArray.length(); i++) {
                achievements.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), GameAchievement.class));
            }

            return achievements;
        } catch (Exception e) {
            return null;
        }
    }
}


