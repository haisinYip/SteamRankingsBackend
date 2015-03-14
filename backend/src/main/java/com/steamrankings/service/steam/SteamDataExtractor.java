package com.steamrankings.service.steam;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.steamrankings.service.api.achievements.GameAchievement;
import com.steamrankings.service.api.games.SteamGame;
import com.steamrankings.service.api.news.SteamNews;
import com.steamrankings.service.api.profiles.SteamProfile;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

public class SteamDataExtractor {

    public static final String JSON_PLAYER_SUMMARIES_COMMUNITY_VISIBILITY_KEY = "communityvisibilitystate";
    public static final String JSON_PARAMETERS_FORMAT_JSON_VALUE = "json";
    public static final String JSON_PLAYER_SUMMARIES_PLAYERS_KEY = "players";
    public static final String JSON_PLAYER_SUMMARIES_RESPONSE_KEY = "response";
    public static final String JSON_PLAYER_SUMMARIES_LAST_LOG_OFF_KEY = "lastlogoff";
    public static final String JSON_PLAYER_SUMMARIES_AVATAR_ICON_URL_KEY = "avatar";
    public static final String JSON_PLAYER_SUMMARIES_AVATAR_MEDIUM_URL_KEY = "avatarmedium";
    public static final String JSON_PLAYER_SUMMARIES_AVATAR_FULL_URL_KEY = "avatarfull";
    public static final String JSON_PLAYER_SUMMARIES_CITY_KEY = "loccityid";
    public static final String JSON_PLAYER_SUMMARIES_PROVINCE_KEY = "locstatecode";
    public static final String JSON_PLAYER_SUMMARIES_COUNTRY_KEY = "loccountrycode";
    public static final String JSON_PLAYER_SUMMARIES_REAL_NAME_KEY = "realname";
    public static final String JSON_PLAYER_SUMMARIES_PERSONA_NAME_KEY = "personaname";
    public static final String JSON_PLAYER_SUMMARIES_PROFILE_URL_KEY = "profileurl";
    public static final String JSON_PLAYER_SUMMARIES_STEAM_ID_KEY = "steamid";
    final static private String STEAM_MEDIA_URL = "http://media.steampowered.com/steamcommunity/public/images/apps/";
    final static private int AVG_NUM_ACHEIVEMENTS_PER_GAME = 10;

    final public static String METHOD_GET_NEWS_FOR_GAME = "GetNewsForApp";
    final public static int VERSION_ONE = 1;
    final public static int VERSION_TWO = 2;
    final public static String PARAMETER_COUNT = "count";
    final public static String PARAMETER_MAXLENGTH = "maxlength";


    private static final Logger logger = Logger.getLogger(SteamDataExtractor.class.getName());

    private SteamApi steamApi;
    final static long INVALID_STEAMID_64 = -1;

    public SteamDataExtractor(SteamApi steamApi) {
        this.steamApi = steamApi;
    }

    /**
     * Gets the profile information of a player
     *
     * @param steamId SteamId of player
     * @return A profile object representing known information about the player.
     */
    public SteamProfile getSteamProfile(long steamId) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(SteamApi.PARAMETER_STEAM_IDS, Long.toString(steamId));
        String jsonString = steamApi.getJSON(SteamApi.INTERFACE_STEAM_USER, SteamApi.METHOD_GET_PLAYER_SUMMARIES, SteamApi.VERSION_TWO, parameters);

        JSONObject json;
        try {
            json = new JSONObject(jsonString).getJSONObject("response").getJSONArray("players").getJSONObject(0);
            if (json.getInt("communityvisibilitystate") != 3) {
                return null;
            } else {
                System.out.println(json.getLong("lastlogoff"));
                System.out.println(new DateTime(0).getMillisOfSecond());
                System.out.println(new DateTime(0).getMillis());
                System.out.println(new DateTime(1425003477).getMillisOfSecond());
                System.out.println(new DateTime(1425003477).getMillis());
                return new SteamProfile(Long.parseLong(json.getString("steamid")), getCommunityIdFromUrl(json.getString("profileurl")), json.getString("personaname"),
                        json.has("realname") ? json.getString("realname") : null, json.has("loccountrycode") ? json.getString("loccountrycode") : null,
                        json.has("locstatecode") ? json.getString("locstatecode") : null, json.has("loccityid") ? Integer.toString(json.getInt("loccityid")) : null,
                        json.has("avatarfull") ? json.getString("avatarfull") : null, json.has("avatarmedium") ? json.getString("avatarmedium") : null, json.has("avatar") ? json.getString("avatar")
                                : null, json.has("lastlogoff") ? new DateTime(json.getLong("lastlogoff")) : new DateTime(0));
            }
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Gets the profile information of several players
     *
     * @param steamId Array of player Steam IDs
     * @return A list of profile objects representing known information about
     * the players.
     */
    public ArrayList<SteamProfile> getSteamProfileThreaded(long[] steamId) {

        // Create a map for constant parameters that don't change every request
        // - i.e. nothing
        HashMap<String, String> parametersConstant = new HashMap<>(0);

        // Create list for arguments that do change each request - i.e. SteamIDs
        // Note that we can specify up to 100 IDs per request, so there might
        // only be a few requests at most
        ArrayList<Map<String, String>> parameterList = new ArrayList<>(1);

        // Find out how many requests we need
        int numRequests = (steamId.length / 100) + 1;
        int numRequestsRemaining = steamId.length;
        // Create each request
        for (int i = 0; i < numRequests; i++) {
            HashMap<String, String> parametersVarying = new HashMap<>(1);

            // Determine number of IDs in request
            int numIdInRequest = Math.min(numRequestsRemaining, 100);
            numRequestsRemaining -= numIdInRequest;

            // Fill request
            String IdList = "";
            for (int j = 0; j < numIdInRequest; j++) {
                IdList += Long.toString(steamId[i * 100 + j]);
                if (j + 1 < numIdInRequest) {
                    IdList += ',';
                }

            }

            parametersVarying.put(SteamApi.PARAMETER_STEAM_IDS, IdList);
            parameterList.add(parametersVarying);
        }

        // Access Steam API with requests
        String[] jsonString = steamApi.getJSONThreaded(SteamApi.INTERFACE_STEAM_USER, SteamApi.METHOD_GET_PLAYER_SUMMARIES, SteamApi.VERSION_TWO, parametersConstant, parameterList);

        ArrayList<SteamProfile> profileList = new ArrayList<>(jsonString.length);
        // Go through each response
        for (int i = 0; i < jsonString.length; i++) {
            try {
                // Each response can have up to 100 profiles, go over them now
                JSONArray players = new JSONObject(jsonString[i]).getJSONObject("response").getJSONArray("players");
                for (int j = 0; j < players.length(); j++) {
                    JSONObject player = players.getJSONObject(j);
                    // Private Profile
                    if (player.getInt("communityvisibilitystate") != 3) {
                        profileList.add(new SteamProfile(Long.parseLong(player.getString("steamid")), getCommunityIdFromUrl(player.getString("profileurl")), player.getString("personaname"), player
                                .has("avatarfull") ? player.getString("avatarfull") : null, player.has("avatarmedium") ? player.getString("avatarmedium") : null, player.has("avatar") ? player
                                        .getString("avatar") : null, player.has("lastlogoff") ? new DateTime(player.getInt("lastlogoff")) : new DateTime(0), player.getInt("communityvisibilitystate")));
                    } // Public profile
                    else {
                        profileList.add(new SteamProfile(Long.parseLong(player.getString("steamid")), getCommunityIdFromUrl(player.getString("profileurl")), player.getString("personaname"), player
                                .has("realname") ? player.getString("realname") : null, player.has("loccountrycode") ? player.getString("loccountrycode") : null, player.has("locstatecode") ? player
                                        .getString("locstatecode") : null, player.has("loccityid") ? Integer.toString(player.getInt("loccityid")) : null, player.has("avatarfull") ? player
                                        .getString("avatarfull") : null, player.has("avatarmedium") ? player.getString("avatarmedium") : null, player.has("avatar") ? player.getString("avatar") : null, player
                                .has("lastlogoff") ? new DateTime(player.getInt("lastlogoff")) : new DateTime(0)));
                    }
                }

            } catch (JSONException e) {
                logger.log(Level.WARNING, "Error parsing JSON for profile request # {0}", i);
            }
        }
        return profileList;

    }

    /**
     * Gets a list of all games a player owns
     *
     * @param steamId The steamId64 of the player
     * @return A mapping of all the games they own to the play time in each
     */
    public Map<SteamGame, Integer> getPlayerOwnedGames(long steamId) {
        // Create parameter list to pass to API
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(SteamApi.PARAMETER_STEAM_ID, Long.toString(steamId));
        parameters.put(SteamApi.PARAMETER_FORMAT, "json");
        parameters.put("include_appinfo", "1");
        parameters.put("include_played_free_games", "1");

        // Call API
        String jsonString = steamApi.getJSON(SteamApi.INTERFACE_PLAYER_SERVICE, SteamApi.METHOD_GET_OWNED_GAMES, SteamApi.VERSION_ONE, parameters);

        JSONArray json;
        HashMap<SteamGame, Integer> games = null;
        try {
            // Retrive game section, ignore other info
            json = new JSONObject(jsonString).getJSONObject("response").getJSONArray("games");
            // Allocate HashMap using number of games
            games = new HashMap<>(json.length());
            // Iterate over all games, add to map
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                games.put(new SteamGame(jsonObject.getInt("appid"), jsonObject.has("img_icon_url") ? getSteamMediaUrl(jsonObject.getInt("appid"), jsonObject.getString("img_icon_url")) : null,
                        jsonObject.has("img_logo_url") ? getSteamMediaUrl(jsonObject.getInt("appid"), jsonObject.getString("img_logo_url")) : null, jsonObject.getString("name")), jsonObject
                        .getInt("playtime_forever"));
            }
        } catch (JSONException e) {
            logger.log(Level.WARNING, "Error parsing JSON for owned games of player {0}", steamId);
        }

        return games;
    }

    /**
     * @deprecated use {@link #getGameAchievementsThreaded()} instead.
     */
    @Deprecated
    public List<GameAchievement> getGameAchievements(int appId) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(SteamApi.PARAMETER_APP_ID, Integer.toString(appId));
        parameters.put(SteamApi.PARAMETER_FORMAT, "json");
        String jsonString = steamApi.getJSON(SteamApi.INTERFACE_STEAM_USER_STATS, SteamApi.METHOD_GET_SCHEMA_FOR_GAME, SteamApi.VERSION_TWO, parameters);

        JSONArray json;
        ArrayList<GameAchievement> achievements = new ArrayList<>();
        try {
            json = new JSONObject(jsonString).getJSONObject("game").getJSONObject("availableGameStats").getJSONArray("achievements");
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                achievements.add(new GameAchievement(appId, jsonObject.getString("name"), jsonObject.has("displayName") ? jsonObject.getString("displayName") : null,
                        jsonObject.has("description") ? jsonObject.getString("description") : null, jsonObject.has("icon") ? jsonObject.getString("icon") : null,
                        jsonObject.has("icongray") ? jsonObject.getString("icongray") : null, DateTime.now()));
            }
        } catch (JSONException e) {
            return achievements;
        }

        return achievements;
    }

    /**
     * Gets all game achievements for all specified games.
     *
     * @param appId List of games to query.
     * @return A list of all achievements for all specified games.
     */
    public List<GameAchievement> getGameAchievementsThreaded(int[] appId) {

        // Create map for arguments that don't change every request
        HashMap<String, String> parametersConstant = new HashMap<>(1);
        parametersConstant.put(SteamApi.PARAMETER_FORMAT, "json");

        // Create and fill list for arguments that do change each request (i.e.
        // appId)
        ArrayList<Map<String, String>> parameterList = new ArrayList<>(appId.length);

        for (int i = 0; i < appId.length; i++) {
            HashMap<String, String> parametersVarying = new HashMap<>(1);
            parametersVarying.put(SteamApi.PARAMETER_APP_ID, Integer.toString(appId[i]));
            parameterList.add(parametersVarying);
        }

        // Send off to steam
        String[] jsonString = steamApi.getJSONThreaded(SteamApi.INTERFACE_STEAM_USER_STATS, SteamApi.METHOD_GET_SCHEMA_FOR_GAME, SteamApi.VERSION_TWO, parametersConstant, parameterList);

        // Parse everything into a nice array list
        JSONArray json;
        // Preallocate memory to avoid reallocation during .add() later
        ArrayList<GameAchievement> achievements = new ArrayList<>(jsonString.length * AVG_NUM_ACHEIVEMENTS_PER_GAME);
        // Iterate over all games
        for (int j = 0; j < jsonString.length; j++) {
            try {
                // Get achievements; ignore other data
                json = new JSONObject(jsonString[j]).getJSONObject("game").getJSONObject("availableGameStats").getJSONArray("achievements");
                // Create object for each achievement in the list
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonObject = json.getJSONObject(i);
                    // Note we set the name to upper case because this name is used
                    // to ID achievements yet Steam is not consistent with the
                    // case when different achievement-related methods are used
                    achievements.add(new GameAchievement(appId[j], jsonObject.getString("name").toUpperCase(Locale.ROOT), jsonObject.has("displayName") ? jsonObject.getString("displayName") : null, jsonObject
                            .has("description") ? jsonObject.getString("description") : null, jsonObject.has("icon") ? jsonObject.getString("icon") : null, jsonObject.has("icongray") ? jsonObject
                                    .getString("icongray") : null, DateTime.now()));
                }
            } catch (JSONException e) {
                logger.log(Level.WARNING, "Error parsing JSON, likely no achievement data for game ID {0}", appId[j]);
            }
        }

        return achievements;
    }

    /**
     * @deprecated use {@link #getPlayerAchievementsThreaded()} instead.
     */
    @Deprecated
    public List<GameAchievement> getPlayerAchievements(long steamId, int appId) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(SteamApi.PARAMETER_APP_ID, Integer.toString(appId));
        parameters.put(SteamApi.PARAMETER_STEAM_ID, Long.toString(steamId));
        parameters.put(SteamApi.PARAMETER_FORMAT, "json");
        String jsonString = steamApi.getJSON(SteamApi.INTERFACE_STEAM_USER_STATS, SteamApi.METHOD_PLAYER_ACHIEVEMENTS, SteamApi.VERSION_ONE, parameters);

        JSONArray json;
        ArrayList<GameAchievement> achievements = new ArrayList<>();
        try {
            json = new JSONObject(jsonString).getJSONObject("playerstats").getJSONArray("achievements");
            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = json.getJSONObject(i);
                if (jsonObject.getInt("achieved") == 1) {
                    achievements.add(new GameAchievement(appId, jsonObject.getString("apiname")));
                }
            }
        } catch (JSONException e) {
            return achievements;
        }

        return achievements;
    }

    /**
     * Gets all achievements for the list of games, which should be owned by the
     * player specified by steamId.
     *
     * @param steamId The steamId64 of the user
     * @param appId A list of appIds to check achievement data
     * @param completionRate Array which will be filled with completion rate
     * corresponding to each game, in the same order as appId Please preallocate
     * an array of the correct size before calling this method.
     * @return All achievements the player owns
     */
    public List<GameAchievement> getPlayerAchievementsThreaded(long steamId, int[] appId, float completionRate[]) {

        // Create map for arguments that don't change every request
        HashMap<String, String> parametersConstant = new HashMap<>(1);
        parametersConstant.put(SteamApi.PARAMETER_FORMAT, "json");
        parametersConstant.put(SteamApi.PARAMETER_STEAM_ID, Long.toString(steamId));
        // This provides some human readable info like name; what it is set to
        // doesn't seem to matter, it's always English
        parametersConstant.put(SteamApi.PARAMETER_LANGUAGE, "en");

        // Create and fill list for arguments that do change each request (i.e.
        // appId)
        ArrayList<Map<String, String>> parameterList = new ArrayList<>(appId.length);

        for (int i = 0; i < appId.length; i++) {
            HashMap<String, String> parametersVarying = new HashMap<>(1);
            parametersVarying.put(SteamApi.PARAMETER_APP_ID, Integer.toString(appId[i]));
            parameterList.add(parametersVarying);
        }

        // Go get data from steam.
        String[] jsonString = steamApi.getJSONThreaded(SteamApi.INTERFACE_STEAM_USER_STATS, SteamApi.METHOD_PLAYER_ACHIEVEMENTS, SteamApi.VERSION_ONE, parametersConstant, parameterList);

        // Parse everything into a nice array list
        JSONArray json;
        // Note we try and preallocate memory here to avoid having to reallocate
        // during .add
        ArrayList<GameAchievement> achievements = new ArrayList<>(jsonString.length * AVG_NUM_ACHEIVEMENTS_PER_GAME);
        for (int j = 0; j < jsonString.length; j++) {
            try {
                // We get the achievement info; we don't care about other info
                json = new JSONObject(jsonString[j]).getJSONObject("playerstats").getJSONArray("achievements");
                // Set achievement counter
                float numAcheivForGame = json.length();
                float numAcheivedByPlayer = 0;
                // Iterate through all achievements
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonObject = json.getJSONObject(i);
                    // Only add achievement if achieved and count it
                    if (jsonObject.getInt("achieved") == 1) {
                        // Note we set the apiname to upper case because this name is used
                        // to ID achievements yet Steam is not consistent with the
                        // case when different achievement-related methods are used
                        achievements.add(new GameAchievement(appId[j], jsonObject.getString("apiname").toUpperCase(Locale.ROOT), jsonObject.getString("name")));
                        numAcheivedByPlayer++;
                    }
                }
                // Calculate completion rate
                completionRate[j] = (numAcheivedByPlayer / numAcheivForGame) * 100;
            } catch (JSONException e) {
                logger.log(Level.WARNING, "Error parsing JSON, likely no achievement data for game ID {0}", appId[j]);
            }
        }

        return achievements;

    }

    public HashMap<Integer, HashMap<String, Double>> getGlobalAchievementPercent(ArrayList<Integer> appId) {
        // Create map for arguments that don't change every request
        HashMap<String, String> parametersConstant = new HashMap<>(1);
        parametersConstant.put(SteamApi.PARAMETER_FORMAT, "json");

        // Create and fill list for arguments that do change each request (i.e.
        // appId)
        ArrayList<Map<String, String>> parameterList = new ArrayList<>(appId.size());

        for (Integer i : appId) {
            HashMap<String, String> parametersVarying = new HashMap<>(1);
            parametersVarying.put(SteamApi.PARAMETER_GAME_ID, Integer.toString(i));
            parameterList.add(parametersVarying);
        }

        // Go get data from steam.
        String[] jsonString = steamApi.getJSONThreaded(SteamApi.INTERFACE_STEAM_USER_STATS, SteamApi.METHOD_GET_GLOBAL_ACHIEVEMENTS_PERCENT, SteamApi.VERSION_TWO, parametersConstant, parameterList);

        // Parse everything into a nice array list
        JSONArray json;
        // Note we try and preallocate memory here
        HashMap<Integer, HashMap<String, Double>> gameList = new HashMap<>(jsonString.length);

        // Iterate over all entries (games)
        for (int j = 0; j < jsonString.length; j++) {
            try {
                // Get the array of achievement percentages for the game
                json = new JSONObject(jsonString[j]).getJSONObject("achievementpercentages").getJSONArray("achievements");

                HashMap<String, Double> achievements = new HashMap<>(json.length());
                // Iterate through all achievements, add to map
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonObject = json.getJSONObject(i);
                    // Note we set the name to upper case because this name is used
                    // to ID achievements yet Steam is not consistent with the
                    // case when different achievement-related methods are used
                    achievements.put(jsonObject.getString("name").toUpperCase(Locale.ROOT), jsonObject.getDouble("percent"));
                }
                // Add achievement map for game to list of games
                gameList.put(appId.get(j), achievements);

            } catch (JSONException e) {
                logger.log(Level.WARNING, "Error parsing JSON, likely no achievement data for game ID {0}", appId.get(j));
            }
        }

        return gameList;
    }

    /**
	 * Gets all news for a specified game.
	 * 
	 * @param appId
	 *            Application ID. In this case, it specifies a game type id.
	 * @param count
	 *            Number of news for specified game.
	 * @param maxlength
	 * 			  Max char length of HTML content.
	 *            
	 * @return All news for specified game
	 */
	public List<SteamNews> getGameNews(int appId, int count, int maxlength) {

		// Create map for arguments that don't change every request
		HashMap<String, String> parametersConstant = new HashMap<String, String>(1);
		parametersConstant.put(SteamApi.PARAMETER_FORMAT, "json");
		parametersConstant.put(SteamApi.PARAMETER_APP_ID, Integer.toString(appId));
		parametersConstant.put(SteamApi.PARAMETER_COUNT, Integer.toString(count));
		parametersConstant.put(SteamApi.PARAMETER_MAXLENGTH, Integer.toString(maxlength));

		// Get data from steam
		String jsonString = steamApi.getJSON(SteamApi.INTERFACE_STEAM_NEWS, SteamApi.METHOD_GET_NEWS_FOR_GAME, SteamApi.VERSION_TWO, parametersConstant);

		JSONObject json = new JSONObject(jsonString).getJSONObject("appnews");
		JSONArray jsonNews;
		
		// Note we try and preallocate memory here to avoid having to reallocate
		// during .add
		ArrayList<SteamNews> gameNews = new ArrayList<SteamNews>(count);

		try {
			jsonNews = json.getJSONArray("newsitems");
			// Iterate through all news
			for (int i = 0; i < jsonNews.length(); i++) {
				JSONObject jsonObject = jsonNews.getJSONObject(i);
				gameNews.add( new SteamNews(appId, jsonObject.getString("title"), jsonObject.getString("url"), new DateTime(jsonObject.getInt("date"))));
			}
		} catch (JSONException e) {
			logger.warning("Error parsing JSON Steam News of game " + appId);
		}
		return gameNews;
	}

// expects either communityid or the steamid64 itself
    public static long convertToSteamId64(String idToConvert) {
        try {
            return Long.parseLong(idToConvert);
        } catch (NumberFormatException e1) {
            try {
                long steamid64 = Long.parseLong(getSteamId64FromXML(SteamApi.getXML(idToConvert)));
                return steamid64;
            } catch (Exception e2) {
                return SteamDataExtractor.INVALID_STEAMID_64;
            }
        }
    }

    private static String getSteamMediaUrl(int appId, String imageHash) {
        return SteamDataExtractor.STEAM_MEDIA_URL + Integer.toString(appId) + "/" + imageHash + ".jpg";
    }

    private static String getCommunityIdFromUrl(String url) {
        String[] urlContents = url.split("/");
        if (urlContents.length <= 0) {
            return "";
        } else {
            return urlContents[4];
        }
    }

    public static String getSteamId64FromXML(String xml) {
        String steamID64 = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("steamID64");
            Node steamid64Node = nodes.item(0).getFirstChild();
            steamID64 = steamid64Node.getNodeValue();

        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            return null;
        }
        return steamID64;
    }
}
