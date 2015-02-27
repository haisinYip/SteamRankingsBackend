package com.steamrankings.service.api.profiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
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

        try {
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