package com.steamrankings.service.api.games;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;

public class Games {
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

    public static List<SteamGame> getPlayedSteamGames(String steamID64) {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:6789/games?id=" + steamID64);
        HttpResponse response = null;

        try {
            response = client.execute(request);
            String data = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            JSONArray jsonArray = new JSONArray(data);
            ArrayList<SteamGame> games = new ArrayList<SteamGame>();

            for (int i = 0; i < jsonArray.length(); i++) {
                games.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), SteamGame.class));
            }

            return games;
        } catch (Exception e) {
            return null;
        }
    }
}