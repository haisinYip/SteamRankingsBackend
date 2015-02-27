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

        String data = client.excecuteRequest("leaderboards?type=achievements&from=" + fromRank + "&to=" + toRank);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JSONArray jsonArray = new JSONArray(data);
            ArrayList<RankEntryByAchievements> ranks = new ArrayList<RankEntryByAchievements>();

            for (int i = 0; i < jsonArray.length(); i++) {
                ranks.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), RankEntryByAchievements.class));
            }

            return ranks;
        } catch (Exception e) {
            return null;
        }
    }
}