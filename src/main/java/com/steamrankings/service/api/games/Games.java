package com.steamrankings.service.api.games;

import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

public class Games {
    public static SteamGame getSteamGame(int appId) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:6789/gamestats?appId=" + appId);
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
        SteamGame game = null;

        try {
            game = mapper.readValue(is, SteamGame.class);
        } catch (Exception e) {
            return null;
        }

        try {
            is.close();
        } catch (Exception e) {
            return null;
        }

        return game;
    }

    public static List<SteamGame> getPlayedSteamGames(String steamID64) {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://mikemontreal.ignorelist.com:6789/gamesowned?id=" + steamID64);
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
        UserOwnedGames games = null;

        try {
            games = mapper.readValue(is, UserOwnedGames.class);
        } catch (Exception e) {
            return null;
        }

        try {
            is.close();
        } catch (Exception e) {
            return null;
        }

        return games.getGames();
    }

    public static int getPlayTime(int appId, String steamID64) {
        return 0;

    }
}