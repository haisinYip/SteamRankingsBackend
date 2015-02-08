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

import com.steamrankings.service.client.SteamRankingsClient;

public class Profiles {
    public static HttpEntity getSteamUser(String steamID64, SteamRankingsClient client) {
    	
    	try
    	{
    	HttpEntity response=client.excecuteRequest("profile?id=" + steamID64);
    	String data=EntityUtils.toString(response);
//        HttpClient client = new DefaultHttpClient();
//        HttpGet request = new HttpGet("http://localhost:6789/profile?id=" + steamID64);
//        HttpResponse response = null;
//
//        try {
//            response = client.execute(request);
            System.out.println(data);
            //ObjectMapper mapper = new ObjectMapper();
            //return mapper.readValue(data, SteamProfile.class);
            return response;
        } catch (Exception e) {
        	System.out.println("Id not found");
            return null;
        }
    }

    public static List<SteamProfile> getSteamFriends(String steamID64) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:6789/friends?id=" + steamID64);
        HttpResponse response = null;

        try {
            response = client.execute(request);
            String data = EntityUtils.toString(response.getEntity());

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