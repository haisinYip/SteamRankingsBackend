package com.steamrankings.service.api.profiles;

import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

public class Profiles {
    public static SteamProfile getSteamUser(String steamID64) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://mikemontreal.ignorelist.com:6789/profile?id=" + steamID64);
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
        SteamProfile profile = null;

        try {
            profile = mapper.readValue(is, SteamProfile.class);
        } catch (Exception e) {
            return null;
        }

        try {
            is.close();
        } catch (Exception e) {
            return null;
        }

        return profile;
    }

    public static List<SteamProfile> getSteamFriends(String steamID64) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://mikemontreal.ignorelist.com:6789/friends?id=" + steamID64);
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
        List<SteamProfile> profile = null;

        try {
            // profile = mapper.readValue(is, SteamProfile.class);
        } catch (Exception e) {
            return null;
        }

        try {
            is.close();
        } catch (Exception e) {
            return null;
        }

        return profile;
    }
}