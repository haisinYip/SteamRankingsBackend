package com.steamrankings.service.api.games;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;

import com.steamrankings.service.api.APIException;
import com.steamrankings.service.api.SteamRankingsClient;
import com.steamrankings.service.api.achievements.GameAchievement;
import com.steamrankings.service.api.leaderboards.RankEntryByTotalPlayTime;

public class Games {
    public static SteamGame getSteamGame(int appId, SteamRankingsClient client) throws APIException, JsonParseException, JsonMappingException, IOException {
        String data = client.excecuteRequest("games?appId=" + appId);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(data, SteamGame.class);
    }
    
    //get rarest achievments
    public static List<GameAchievement> getRarestAchievements(int appId, SteamRankingsClient client) throws APIException, JsonParseException, JsonMappingException,JSONException, IOException {
        String data = client.excecuteRequest("games?appIdRarest=" + appId);
      
        ObjectMapper mapper = new ObjectMapper();
        JSONArray jsonArray = new JSONArray(data);
        ArrayList<GameAchievement> rarestAchievements = new ArrayList<GameAchievement>();

        for (int i = 0; i < jsonArray.length(); i++) {
        	rarestAchievements.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), GameAchievement.class));
        }
        
        return rarestAchievements;
    }

    public static List<SteamGame> getPlayedSteamGames(String steamID64, SteamRankingsClient client) throws APIException, JsonParseException, JsonMappingException, JSONException, IOException {
        String data = client.excecuteRequest("games?id=" + steamID64);

        ObjectMapper mapper = new ObjectMapper();
        JSONArray jsonArray = new JSONArray(data);
        ArrayList<SteamGame> games = new ArrayList<SteamGame>();

        for (int i = 0; i < jsonArray.length(); i++) {
            games.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), SteamGame.class));
        }

        return games;
    }

    public static List<SteamGame> getSteamGames(SteamRankingsClient client) throws JsonParseException, JsonMappingException, JSONException, IOException, APIException {
        String data = client.excecuteRequest("games");
        
        ObjectMapper mapper = new ObjectMapper();
        JSONArray jsonArray = new JSONArray(data);
        ArrayList<SteamGame> games = new ArrayList<SteamGame>();
        
        for(int i = 0; i < jsonArray.length(); i++) {
            games.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), SteamGame.class));
        }
        
        return games;
    }

    public static List<RankEntryByTotalPlayTime> getRanksByTotalPlayTime(int fromRank, int toRank, SteamRankingsClient client) {
        return null;
    }
}