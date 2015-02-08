package com.steamrankings.service.steam;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class SteamApi {
    private String apiKey;
    private static DefaultHttpClient httpClient = null;

    final public static String INTERFACE_STEAM_USER = "ISteamUser";
    final public static String INTERFACE_STEAM_USER_STATS = "ISteamUserStats";
    final public static String INTERFACE_PLAYER_SERVICE = "IPlayerService";

    final public static String METHOD_GET_PLAYER_SUMMARIES = "GetPlayerSummaries";
    final public static String METHOD_GET_OWNED_GAMES = "GetOwnedGames";
    final public static String METHOD_PLAYER_ACHIEVEMENTS = "GetPlayerAchievements";
    final public static String METHOD_GET_SCHEMA_FOR_GAME = "GetSchemaForGame";

    final public static int VERSION_ONE = 1;
    final public static int VERSION_TWO = 2;

    final public static String PARAMETER_STEAM_IDS = "steamids";
    final public static String PARAMETER_STEAM_ID = "steamid";
    final public static String PARAMETER_APP_ID = "appid";
    final public static String PARAMETER_FORMAT = "format";

    public SteamApi(String apiKey) {
        this.apiKey = apiKey;
    }

    public static void main(String[] args) {
        SteamApi api = new SteamApi("3A7D85F3F85FE936F9573F9BDF559089");
        System.out.println("PlayerSummaries");
        System.out.println();
        System.out.println();
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("steamids", "76561197965726621");
        System.out.println(api.getJSON(INTERFACE_STEAM_USER, METHOD_GET_PLAYER_SUMMARIES, VERSION_TWO, parameters));
        try {
        	httpClient = new DefaultHttpClient();
        }
        catch (Exception e) {
            return;
        }
    }

    public String getJSON(String apiInterface, String method, int version, Map<String, String> parameters) {
        String url = String.format("https://api.steampowered.com/%s/%s/v%04d/?", apiInterface, method, version);
        parameters.put("key", apiKey);

        boolean first = true;
        for (Entry<String, String> parameter : parameters.entrySet()) {
            if (first) {
                first = false;
            } else {
                url += '&';
            }

            url += String.format("%s=%s", parameter.getKey(), parameter.getValue());
        }

        String data;

        try {
            if (this.httpClient == null) {
            	httpClient = new DefaultHttpClient();
            }
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            data = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            return null;
        }

        return data;
    }
}
