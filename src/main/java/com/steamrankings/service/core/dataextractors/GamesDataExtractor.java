package com.steamrankings.service.core.dataextractors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.steam.community.WebApi;
import com.steamrankings.service.api.games.Game;

public class GamesDataExtractor {

    private String APIKEY = "3A7D85F3F85FE936F9573F9BDF559089";
    String id64;
    int appId;
    int[] appIds;
    String iconUrl;
    String logoUrl;
    String name;

    public GamesDataExtractor(String id64) {
        this.id64 = id64;
    }

    public Game addGames() throws Exception {

        WebApi.setApiKey(APIKEY);
        Game game = new Game(appId, iconUrl, logoUrl, name);

        Map<String, Object> param = new HashMap<String, Object>();

        param.put("steamid", id64);
        param.put("include_app_info", 1);

        String jsonString = WebApi.getJSON("IPlayerService", "GetOwneGames", 1, param);

        System.out.println(jsonString);

        JSONObject json = new JSONObject(jsonString);
        System.out.println(json);

        json = (JSONObject) json.get("response");
        System.out.println(json);

        JSONArray jsonArray = json.getJSONArray("games");
        System.out.println(jsonArray);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonElement = jsonArray.getJSONObject(i);
            @SuppressWarnings("rawtypes")
            Iterator itr = jsonElement.keys();
            while (itr.hasNext()) {
                String element = (String) itr.next();
                if (element.equals("appid")) {
                    String appid = jsonElement.getString("appid");
                    System.out.println(appid);
                    game.setAppId(Integer.parseInt(appid));
                    System.out.println(appid);
                } else if (element.equals("name")) {
                    String name = jsonElement.getString("name");
                    System.out.println(name);
                    game.setName(name);
                } else if (element.equals("img_icon_url")) {
                    String iconUrl = jsonElement.getString("img_icon_url");
                    System.out.println(iconUrl);
                    game.setIconUrl(iconUrl);
                } else if (element.equals("img_logo_url")) {
                    String logoUrl = jsonElement.getString("img_logo_url");
                    System.out.println(logoUrl);
                    game.setLogoUrl(logoUrl);
                }
            }
        }
        return game;
    }
}