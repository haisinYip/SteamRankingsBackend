package com.steamrankings.service.api.profiles;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;

import com.steamrankings.service.api.client.SteamRankingsClient;


public class Profiles {
    public static SteamProfile getSteamUser(String steamID64, SteamRankingsClient client) {
    	
    	try
    	{
    	String data=client.excecuteRequest("profile?id=" + steamID64);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(data, SteamProfile.class);
        } catch (Exception e) {
        	System.out.println("Id not found");
            return null;
        }
    }

    public static List<SteamProfile> getSteamFriends(String steamID64, SteamRankingsClient client) {

        try {
        	String data=client.excecuteRequest("friends?id=" + steamID64);

            JSONArray jsonArray = new JSONArray(data);
            ObjectMapper mapper = new ObjectMapper();
            ArrayList<SteamProfile> profiles = new ArrayList<SteamProfile>();

            for (int i = 0; i < jsonArray.length(); i++) {
                profiles.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), SteamProfile.class));
            }

            return profiles;
        } catch (Exception e) {
            return null;
        }
    }
}