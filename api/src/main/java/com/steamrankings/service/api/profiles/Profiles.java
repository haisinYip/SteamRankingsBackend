package com.steamrankings.service.api.profiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;

import com.steamrankings.service.api.APIException;
import com.steamrankings.service.api.SteamRankingsClient;

public class Profiles {
    public static SteamProfile getSteamUser(String steamID64, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
        String data = client.excecuteRequest("profile?id=" + steamID64);
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(data, SteamProfile.class);
    }

    public static List<SteamProfile> getSteamFriends(String steamID64, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
        String data = client.excecuteRequest("friends?id=" + steamID64);

        JSONArray jsonArray = new JSONArray(data);
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<SteamProfile> profiles = new ArrayList<SteamProfile>();

        for (int i = 0; i < jsonArray.length(); i++) {
            profiles.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), SteamProfile.class));
        }

        return profiles;
    }
    
    public static ArrayList<VersusResult> compareSteamUsers(String user1Id, String user2Id, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
        String data = client.excecuteRequest("versus?id1=" + user1Id + "&id2=" + user2Id);

        ObjectMapper mapper = new ObjectMapper();
        JSONArray jsonArray = new JSONArray(data);
        ArrayList<VersusResult> comparisons = new ArrayList<VersusResult>();

        for (int i = 0; i < jsonArray.length(); i++) {
            comparisons.add(mapper.readValue(jsonArray.getJSONObject(i).toString(), VersusResult.class));
        }

        return comparisons;
    }
    
    public static String addBlackList(String steamID64, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
    	String data = client.excecuteRequest("blacklist?id=" + steamID64);

        return data;
    }
    
    public static SteamProfile updateUser(String steamID64, SteamRankingsClient client) throws ClientProtocolException, APIException, IOException {
        String data = client.excecuteRequest("update?id=" + steamID64);
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(data, SteamProfile.class);
    }
    
    public static String getTopCountryPlayer(String countryCode, SteamRankingsClient client) throws JsonParseException, JsonMappingException, IOException, APIException {
    	return client.excecuteRequest("topplayer?country=" + countryCode);
    }
    
    public static String getTopGamePlayer(String gameId, SteamRankingsClient client) throws JsonParseException, JsonMappingException, IOException, APIException {
    	return client.excecuteRequest("topplayer?game=" + gameId);
    }
}