package com.steamrankings.service.api.profiles;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;

import com.steamrankings.service.api.APIException;

public class Profiles {
	
	 private static final String API_ERROR_BAD_ARGUMENTS_CODE = "1000";
	    private static final String API_ERROR_STEAM_USER_DOES_NOT_EXIST = "2000";
	    private static final String API_ERROR_STEAM_ID_INVALID = "3000";
	    
    public static SteamProfile getSteamUser(String steamID64) throws Exception {
        HttpClient client = new DefaultHttpClient();
        try {
            HttpGet request = new HttpGet("http://localhost:6789/profile?id=" + steamID64);
            HttpResponse response = null;
            
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
            return mapper.readValue(data, SteamProfile.class);
        } catch (Exception e) {
            if (e instanceof APIException) {
            	throw e;
            }
            if (e instanceof IllegalArgumentException) {
            	throw e;
            }
        }
		return null;
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
    public static void addBlackList(String steamID64) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://localhost:6789/blacklist?id=" + steamID64);
        HttpResponse response = null;

        try {
            response = client.execute(request);
        } catch (Exception e) {
            
        }
    }
}