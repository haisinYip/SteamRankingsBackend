package com.steamrankings.service.api.leaderboards;

import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

public class Leaderboards {
    public static List<RankEntryByAchievements> getRanksByAchievementTotal(int fromRank, int toRank) {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://mikemontreal.ignorelist.com:6789/ranksbyachievments?fromrank=" + fromRank + "&torank=" + toRank);
        HttpResponse response = null;

        try {
            response = client.execute(request);
        } catch (Exception e) {
            return null;
        }

        HttpEntity entity = response.getEntity();
        InputStream is = null;
        try {
            is = entity.getContent();
        } catch (Exception e) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        RankEntryByAchievementListWrapper rankByAchievements = null;

        try {
            rankByAchievements = mapper.readValue(is, RankEntryByAchievementListWrapper.class);
        } catch (Exception e) {
            return null;
        }

        try {
            is.close();
        } catch (Exception e) {
            return null;
        }

        return rankByAchievements.getRankEntryByAchievements();
    }
}
