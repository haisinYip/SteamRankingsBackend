package com.steamrankings.service.api.leaderboards;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;

public class Leaderboards {
    public static List<RankEntryByAchievements> getRanksByAchievementTotal(int fromRank, int toRank) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:6789/leaderboards?type=achievements&from=" + fromRank + "&to=" + toRank);
        HttpResponse response = null;

        try {
            response = client.execute(request);
            String data = EntityUtils.toString(response.getEntity());

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