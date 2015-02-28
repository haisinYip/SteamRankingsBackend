package com.steamrankings.service.api.games;

import java.util.ArrayList;
import java.util.List;

import com.steamrankings.service.api.APIException;
import com.steamrankings.service.api.leaderboards.RankEntryByTotalPlayTime;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;

public class Games {
	
    private static final String API_ERROR_BAD_ARGUMENTS_CODE = "1000";
    private static final String API_ERROR_STEAM_USER_DOES_NOT_EXIST = "2000";
    private static final String API_ERROR_STEAM_ID_INVALID = "3000";
    
    
    public static SteamGame getSteamGame(int appId) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:6789/games?appId=" + appId);
        HttpResponse response = null;

        try {
            response = client.execute(request);
            String data = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(data, SteamGame.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<SteamGame> getPlayedSteamGames(String steamID64) throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:6789/games?id=" + steamID64);
        HttpResponse response = null;

        try {
            response = client.execute(request);
            		
            String data = EntityUtils.toString(response.getEntity());
            
            if (response.getStatusLine().getStatusCode() == 400) {
            	if (data.equals(API_ERROR_STEAM_ID_INVALID)) {
            		throw new APIException("Invalid or private Steam ID");
            	}
            	
            	else if (data.equals(API_ERROR_BAD_ARGUMENTS_CODE)) {
            		throw new APIException("Bad arguments passed to API");
            	}
            	else {
            		throw new APIException("Unknown API exception; HTTP 400 error");
            	}
            }
            else if (response.getStatusLine().getStatusCode() == 404)
            {
            	if (data.equals(API_ERROR_STEAM_USER_DOES_NOT_EXIST))
            	{
            		throw new APIException("Steam user does not exist");
            	}
            	else {
            		throw new APIException("Unknown API exception; HTTP 404 error");
            	}
            }

            ObjectMapper mapper = new ObjectMapper();
            JSONArray jsonArray = new JSONArray(data);
            ArrayList<SteamGame> games = new ArrayList<SteamGame>();

            for (int i = 0; i < jsonArray.length(); i++) {
                games.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), SteamGame.class));
            }

            return games;
        } catch (Exception e) {
            if (e instanceof APIException) {
            	throw e;
            }
        }
		return null;
    }
    
    public static List<SteamGame> getSteamGames(int appid) throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:6789/games");
        HttpResponse response = null;

        try {
            response = client.execute(request);
            		
            String data = EntityUtils.toString(response.getEntity());
            
            if (response.getStatusLine().getStatusCode() == 400) {
            	if (data.equals(API_ERROR_STEAM_ID_INVALID)) {
            		throw new APIException("Invalid or private Steam ID");
            	}
            	
            	else if (data.equals(API_ERROR_BAD_ARGUMENTS_CODE)) {
            		throw new APIException("Bad arguments passed to API");
            	}
            	else {
            		throw new APIException("Unknown API exception; HTTP 400 error");
            	}
            }
            else if (response.getStatusLine().getStatusCode() == 404)
            {
            	if (data.equals(API_ERROR_STEAM_USER_DOES_NOT_EXIST))
            	{
            		throw new APIException("Steam user does not exist");
            	}
            	else {
            		throw new APIException("Unknown API exception; HTTP 404 error");
            	}
            }

            ObjectMapper mapper = new ObjectMapper();
            JSONArray jsonArray = new JSONArray(data);
            ArrayList<SteamGame> games = new ArrayList<SteamGame>();

            for (int i = 0; i < jsonArray.length(); i++) {
                games.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), SteamGame.class));
            }

            return games;
        } catch (Exception e) {
            if (e instanceof APIException) {
            	throw e;
            }
        }
		return null;
    }
    
    public static List<RankEntryByTotalPlayTime> getRanksByTotalPlayTime(int fromRank, int toRank) {
    	HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:6789/leaderboards?type=games&from=" + fromRank + "&to=" + toRank);
        HttpResponse response = null;
        
        try {
        	response = client.execute(request);
        	String data = EntityUtils.toString(response.getEntity());
        	
        	ObjectMapper mapper = new ObjectMapper();
        	JSONArray jsonArray = new JSONArray(data);
        	ArrayList<RankEntryByTotalPlayTime> ranks = new ArrayList<RankEntryByTotalPlayTime>();
        	
        	for(int i = 0; i < jsonArray.length(); i++) {
        		ranks.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), RankEntryByTotalPlayTime.class));
        	}
        	
        	return ranks;
        } catch (Exception e) {
        	return null;
        }	
    }
}