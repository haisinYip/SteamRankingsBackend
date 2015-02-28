package com.steamrankings.service.api.leaderboards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;

import com.steamrankings.service.api.APIException;
import com.steamrankings.service.api.SteamRankingsClient;

public class Leaderboards {
    public static List<RankEntryByAchievements> getRanksByAchievementTotal(int fromRank, int toRank, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
        return getRankingBy("achievements", fromRank, toRank, client);
    }

    public static List<RankEntryByAchievements> getRanksByTotalPlayTime(int fromRank, int toRank, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
        return getRankingBy("games", fromRank, toRank, client);
    }

    public static List<RankEntryByAchievements> getRanksByCompletionRate(int fromRank, int toRank, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
        return getRankingBy("completionrate", fromRank, toRank, client);
    }

    public static List<RankEntryByAchievements> getRanksByCountry(String countryCode, int fromRank, int toRank, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
    	String data = client.excecuteRequest("leaderboards?type=countries?id=" + countryCode + "&from=" + fromRank + "&to=" + toRank);

        ObjectMapper mapper = new ObjectMapper();
        JSONArray jsonArray = new JSONArray(data);
        ArrayList<RankEntryByAchievements> ranks = new ArrayList<RankEntryByAchievements>();

        for (int i = 0; i < jsonArray.length(); i++) {
            ranks.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), RankEntryByAchievements.class));
        }

        return ranks;
    }
    
    private static List<RankEntryByAchievements> getRankingBy(String type, int fromRank, int toRank, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
        String data = client.excecuteRequest("leaderboards?type=" + type + "&from=" + fromRank + "&to=" + toRank);

        ObjectMapper mapper = new ObjectMapper();
        JSONArray jsonArray = new JSONArray(data);
        ArrayList<RankEntryByAchievements> ranks = new ArrayList<RankEntryByAchievements>();

        for (int i = 0; i < jsonArray.length(); i++) {
            ranks.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), RankEntryByAchievements.class));
        }

        return ranks;
    }
}