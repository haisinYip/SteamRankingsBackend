package com.steamrankings.service.api.news;

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

public class News {

    public static List<SteamNews> getSteamNews(int appId, SteamRankingsClient client) throws JsonParseException, JsonMappingException, JSONException, IOException, APIException {
        String data = client.excecuteRequest("news?appId=" + appId);
        
        ObjectMapper mapper = new ObjectMapper();
        JSONArray jsonArray = new JSONArray(data);
        ArrayList<SteamNews> news = new ArrayList<SteamNews>();
        
        for(int i = 0; i < jsonArray.length(); i++) {
        	news.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), SteamNews.class));
        }
        
        return news;
    }
}
