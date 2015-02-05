package com.steamrankings.service.api.achievements;

import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import com.steamrankings.service.api.games.UserOwnedGames;

public class Achievements {
    public static List<SteamAchievement> getSteamGameAchievements(int appId) {
    	 HttpClient client = new DefaultHttpClient();
         HttpGet request = new HttpGet("http://mikemontreal.ignorelist.com:6789/gameachievements?appid=" + appId);
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
         AchievementWrapper achievements = null;

         try {
        	 achievements = mapper.readValue(is, AchievementWrapper.class);
         } catch (Exception e) {
             return null;
         }

         try {
             is.close();
         } catch (Exception e) {
             return null;
         }

         return achievements.getAchievements();
    }

    public static List<SteamAchievement> getUnlockedAchievements(String steamID64) {
    	
   	 HttpClient client = new DefaultHttpClient();
     HttpGet request = new HttpGet("http://mikemontreal.ignorelist.com:6789/userunlockedachievements?id=" + steamID64);
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
     AchievementWrapper achievements = null;

     try {
    	 achievements = mapper.readValue(is, AchievementWrapper.class);
     } catch (Exception e) {
         return null;
     }

     try {
         is.close();
     } catch (Exception e) {
         return null;
     }

     return achievements.getAchievements();	
    	
    }

    public static List<SteamAchievement> getUnlockedAchievementsByGame(String steamID64, int appId) {
      	 HttpClient client = new DefaultHttpClient();
         HttpGet request = new HttpGet("http://mikemontreal.ignorelist.com:6789/userunlockedachievements?id=" + steamID64 +"&appid="+ appId);
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
         AchievementWrapper achievements = null;

         try {
        	 achievements = mapper.readValue(is, AchievementWrapper.class);
         } catch (Exception e) {
             return null;
         }

         try {
             is.close();
         } catch (Exception e) {
             return null;
         }

         return achievements.getAchievements();	
    }
}
