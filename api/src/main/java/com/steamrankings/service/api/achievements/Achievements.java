package com.steamrankings.service.api.achievements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;

import com.steamrankings.service.api.APIException;
import com.steamrankings.service.api.SteamRankingsClient;

public class Achievements {
    public static List<GameAchievement> getGameAchievements(int appId, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
        String data = client.excecuteRequest("achievements?appid=" + appId);

        try {
            return deserializeJSONData(data);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<GameAchievement> getUnlockedAchievements(String steamID64, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
        String data = client.excecuteRequest("achievements?id=" + steamID64);

        try {
            return deserializeJSONData(data);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<GameAchievement> getUnlockedAchievements(String steamID64, int appId, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
        String data = client.excecuteRequest("achievements?id=" + steamID64 + "&appid=" + appId);

        try {
            return deserializeJSONData(data);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<GameAchievement> deserializeJSONData(String data) throws JsonParseException, JsonMappingException, JSONException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        JSONArray jsonArray = new JSONArray(data);
        ArrayList<GameAchievement> achievements = new ArrayList<GameAchievement>();

        for (int i = 0; i < jsonArray.length(); i++) {
            achievements.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), GameAchievement.class));
        }

        return achievements;
    }
}