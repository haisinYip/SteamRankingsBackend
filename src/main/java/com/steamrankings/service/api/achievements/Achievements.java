package com.steamrankings.service.api.achievements;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class Achievements {
    public static List<GameAchievement> getGameAchievements(int appId) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://mikemontreal.ignorelist.com:6789/achievements?appid=" + appId);
        HttpResponse response = null;

        try {
            response = client.execute(request);
            String data = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            AchievementWrapper achievements = mapper.readValue(data, AchievementWrapper.class);

            return achievements.getAchievements();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<GameAchievement> getUnlockedAchievements(String steamID64) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://mikemontreal.ignorelist.com:6789/achievements?id=" + steamID64);
        HttpResponse response = null;

        try {
            response = client.execute(request);
            String data = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            AchievementWrapper achievements = mapper.readValue(data, AchievementWrapper.class);

            return achievements.getAchievements();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<GameAchievement> getUnlockedAchievements(String steamID64, int appId) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://mikemontreal.ignorelist.com:6789/achievements?id=" + steamID64 + "&appid=" + appId);
        HttpResponse response = null;

        try {
            response = client.execute(request);
            String data = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            AchievementWrapper achievements = mapper.readValue(data, AchievementWrapper.class);

            return achievements.getAchievements();
        } catch (Exception e) {
            return null;
        }
    }
}
