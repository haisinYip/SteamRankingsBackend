package com.steamrankings.service.api.leaderboards;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class Leaderboards {
    public static List<RankEntryByAchievements> getRanksByAchievementTotal(int fromRank, int toRank) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:6789/leaderboards?type=achievements&from=" + fromRank + "&to=" + toRank);
        HttpResponse response = null;

        try {
            response = client.execute(request);
            String data = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            RankEntryByAchievementListWrapper achievements = mapper.readValue(data, RankEntryByAchievementListWrapper.class);

            return achievements.getRankEntryByAchievements();
        } catch (Exception e) {
            return null;
        }
    }
}