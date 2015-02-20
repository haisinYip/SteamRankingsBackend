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
import com.steamrankings.service.api.profiles.SteamProfile;

public class SteamDataExtractor {

	final static private String STEAM_MEDIA_URL = "http://media.steampowered.com/steamcommunity/public/images/apps/";
	final static private int AVG_NUM_ACHEIVEMENTS_PER_GAME = 10;

	private static final Logger logger = Logger
			.getLogger(SteamDataExtractor.class.getName());

	private SteamApi steamApi;

	public SteamDataExtractor(SteamApi steamApi) {
		this.steamApi = steamApi;
	}

	/**
	 * Gets the profile information of a player
	 * @param steamId SteamId of player
	 * @return A profile object representing known information about the player.
	 */
	public SteamProfile getSteamProfile(long steamId) {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(SteamApi.PARAMETER_STEAM_IDS, Long.toString(steamId));
		String jsonString = steamApi.getJSON(SteamApi.INTERFACE_STEAM_USER,
				SteamApi.METHOD_GET_PLAYER_SUMMARIES, SteamApi.VERSION_TWO,
				parameters);

		JSONObject json;
		try {
			json = new JSONObject(jsonString).getJSONObject("response")
					.getJSONArray("players").getJSONObject(0);
			if (json.getInt("communityvisibilitystate") != 3) {
				return null;
			} else {
				return new SteamProfile(Long.parseLong(json
						.getString("steamid")),
						getCommunityIdFromUrl(json.getString("profileurl")),
						json.getString("personaname"),
						json.has("realname") ? json.getString("realname")
								: null,
						json.has("loccountrycode") ? json
								.getString("loccountrycode") : null,
						json.has("locstatecode") ? json
								.getString("locstatecode") : null,
						json.has("loccityid") ? Integer.toString(json
								.getInt("loccityid")) : null,
						json.has("avatarfull") ? json.getString("avatarfull")
								: null,
						json.has("avatarmedium") ? json
								.getString("avatarmedium") : null,
						json.has("avatar") ? json.getString("avatar") : null,
						json.has("lastlogoff") ? new DateTime(json
								.getInt("lastlogoff")) : new DateTime(0));
			}
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * Gets a list of all games a player owns
	 * @param steamId The steamId64 of the player
	 * @return A mapping of all the games they own to the play time in each
	 */
	public Map<SteamGame, Integer> getPlayerOwnedGames(long steamId) {
		// Create parameter list to pass to API
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(SteamApi.PARAMETER_STEAM_ID, Long.toString(steamId));
		parameters.put(SteamApi.PARAMETER_FORMAT, "json");
		parameters.put("include_appinfo", "1");
		parameters.put("include_played_free_games", "1");
		
		// Call API
		String jsonString = steamApi.getJSON(SteamApi.INTERFACE_PLAYER_SERVICE,
				SteamApi.METHOD_GET_OWNED_GAMES, SteamApi.VERSION_ONE,
				parameters);

		JSONArray json;
		HashMap<SteamGame, Integer> games = null;
		try {
			// Retrive game section, ignore other info
			json = new JSONObject(jsonString).getJSONObject("response")
					.getJSONArray("games");
			// Allocate HashMap using number of games
			games = new HashMap<SteamGame, Integer>(json.length());
			// Iterate over all games, add to map
			for (int i = 0; i < json.length(); i++) {
				JSONObject jsonObject = json.getJSONObject(i);
				games.put(
						new SteamGame(
								jsonObject.getInt("appid"),
								jsonObject.has("img_icon_url") ? getSteamMediaUrl(
										jsonObject.getInt("appid"),
										jsonObject.getString("img_icon_url"))
										: null,
								jsonObject.has("img_logo_url") ? getSteamMediaUrl(
										jsonObject.getInt("appid"),
										jsonObject.getString("img_logo_url"))
										: null, jsonObject.getString("name")),
						jsonObject.getInt("playtime_forever"));
			}
		} catch (JSONException e) {
			logger.warning("Error parsing JSON for owned games of player " + steamId);
		}

		return games;
	}

	/**
	 * @deprecated use {@link #getGameAchievementsThreaded()} instead.
	 */
	@Deprecated
	public List<GameAchievement> getGameAchievements(int appId) {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(SteamApi.PARAMETER_APP_ID, Integer.toString(appId));
		parameters.put(SteamApi.PARAMETER_FORMAT, "json");
		String jsonString = steamApi.getJSON(
				SteamApi.INTERFACE_STEAM_USER_STATS,
				SteamApi.METHOD_GET_SCHEMA_FOR_GAME, SteamApi.VERSION_TWO,
				parameters);

		JSONArray json;
		ArrayList<GameAchievement> achievements = new ArrayList<GameAchievement>();
		try {
			json = new JSONObject(jsonString).getJSONObject("game")
					.getJSONObject("availableGameStats")
					.getJSONArray("achievements");
			for (int i = 0; i < json.length(); i++) {
				JSONObject jsonObject = json.getJSONObject(i);
				achievements.add(new GameAchievement(appId, jsonObject
						.getString("name"),
						jsonObject.has("displayName") ? jsonObject
								.getString("displayName") : null, jsonObject
								.has("description") ? jsonObject
								.getString("description") : null, jsonObject
								.has("icon") ? jsonObject.getString("icon")
								: null, jsonObject.has("icongray") ? jsonObject
								.getString("icongray") : null, DateTime.now()));
			}
		} catch (JSONException e) {
			return achievements;
		}

		return achievements;
	}

	
	/**
	 * Gets all game achievements for all specified games.
	 * @param appId List of games to query.
	 * @return A list of all achievements for all specified games.
	 */
	public List<GameAchievement> getGameAchievementsThreaded(int[] appId) {

		// Create map for arguments that don't change every request
		HashMap<String, String> parametersConstant = new HashMap<String, String>(
				1);
		parametersConstant.put(SteamApi.PARAMETER_FORMAT, "json");

		// Create and fill list for arguments that do change each request (i.e. appId)
		ArrayList<Map<String, String>> parameterList = new ArrayList<>(
				appId.length);

		for (int i = 0; i < appId.length; i++) {
			HashMap<String, String> parametersVarying = new HashMap<String, String>(
					1);
			parametersVarying.put(SteamApi.PARAMETER_APP_ID,
					Integer.toString(appId[i]));
			parameterList.add(parametersVarying);
		}

		// Send off to steam
		String[] jsonString = steamApi.getJSONThreaded(
				SteamApi.INTERFACE_STEAM_USER_STATS,
				SteamApi.METHOD_GET_SCHEMA_FOR_GAME, SteamApi.VERSION_TWO,
				parametersConstant, parameterList);

		// Parse everything into a nice array list
		JSONArray json;
		// Preallocate memory to avoid reallocation during .add() later
		ArrayList<GameAchievement> achievements = new ArrayList<GameAchievement>(
				jsonString.length * AVG_NUM_ACHEIVEMENTS_PER_GAME);
		// Iterate over all games
		for (int j = 0; j < jsonString.length; j++) {
			try {
				// Get achievements; ignore other data
				json = new JSONObject(jsonString[j]).getJSONObject("game")
						.getJSONObject("availableGameStats")
						.getJSONArray("achievements");
				// Create object for each achievement in the list
				for (int i = 0; i < json.length(); i++) {
					JSONObject jsonObject = json.getJSONObject(i);
					achievements.add(new GameAchievement(appId[j], jsonObject
							.getString("name"),
							jsonObject.has("displayName") ? jsonObject
									.getString("displayName") : null,
							jsonObject.has("description") ? jsonObject
									.getString("description") : null,
							jsonObject.has("icon") ? jsonObject
									.getString("icon") : null, jsonObject
									.has("icongray") ? jsonObject
									.getString("icongray") : null, DateTime
									.now()));
				}
			} catch (JSONException e) {
				logger.warning("Error parsing JSON, likely no achievement data for game ID " + appId[j]);
			}
		}

		return achievements;
	}

	/**
	 * @deprecated use {@link #getPlayerAchievementsThreaded()} instead.
	 */
	@Deprecated
	public List<GameAchievement> getPlayerAchievements(long steamId, int appId) {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(SteamApi.PARAMETER_APP_ID, Integer.toString(appId));
		parameters.put(SteamApi.PARAMETER_STEAM_ID, Long.toString(steamId));
		parameters.put(SteamApi.PARAMETER_FORMAT, "json");
		String jsonString = steamApi.getJSON(
				SteamApi.INTERFACE_STEAM_USER_STATS,
				SteamApi.METHOD_PLAYER_ACHIEVEMENTS, SteamApi.VERSION_ONE,
				parameters);

		JSONArray json;
		ArrayList<GameAchievement> achievements = new ArrayList<GameAchievement>();
		try {
			json = new JSONObject(jsonString).getJSONObject("playerstats")
					.getJSONArray("achievements");
			for (int i = 0; i < json.length(); i++) {
				JSONObject jsonObject = json.getJSONObject(i);
				if (jsonObject.getInt("achieved") == 1) {
					achievements.add(new GameAchievement(appId, jsonObject
							.getString("apiname")));
				}
			}
		} catch (JSONException e) {
			return achievements;
		}

		return achievements;
	}

	/**
	 * Gets all achievements for the list of games, which should be owned by the player specified by steamId.
	 * @param steamId The steamId64 of the user
	 * @param appId A list of appIds to check achievement data
	 * @param completionRate Array which will be filled with completion rate corresponding to each game, in the same order as appId
	 * Please preallocate an array of the correct size before calling this method.
	 * @return All achievements the player owns
	 */
	public List<GameAchievement> getPlayerAchievementsThreaded(long steamId,
			int[] appId, float completionRate[]) {

		// Create map for arguments that don't change every request
		HashMap<String, String> parametersConstant = new HashMap<String, String>(
				1);
		parametersConstant.put(SteamApi.PARAMETER_FORMAT, "json");
		parametersConstant.put(SteamApi.PARAMETER_STEAM_ID,
				Long.toString(steamId));
		// This provides some human readable info like name; what it is set to
		// doesn't seem to matter, it's always English
		parametersConstant.put(SteamApi.PARAMETER_LANGUAGE, "en");

		// Create and fill list for arguments that do change each request (i.e. appId)
		ArrayList<Map<String, String>> parameterList = new ArrayList<>(
				appId.length);

		for (int i = 0; i < appId.length; i++) {
			HashMap<String, String> parametersVarying = new HashMap<String, String>(
					1);
			parametersVarying.put(SteamApi.PARAMETER_APP_ID,
					Integer.toString(appId[i]));
			parameterList.add(parametersVarying);
		}

		// Go get data from steam.
		String[] jsonString = steamApi.getJSONThreaded(
				SteamApi.INTERFACE_STEAM_USER_STATS,
				SteamApi.METHOD_PLAYER_ACHIEVEMENTS, SteamApi.VERSION_ONE,
				parametersConstant, parameterList);

		// Parse everything into a nice array list
		JSONArray json;
		// Note we try and preallocate memory here to avoid having to reallocate during .add
		ArrayList<GameAchievement> achievements = new ArrayList<GameAchievement>(
				jsonString.length * AVG_NUM_ACHEIVEMENTS_PER_GAME);
		for (int j = 0; j < jsonString.length; j++) {
			try {
				// We get the achievement info; we don't care about other info
				json = new JSONObject(jsonString[j]).getJSONObject("playerstats")
						.getJSONArray("achievements");
				// Set achievement counter
				float numAcheivForGame = json.length();
				float numAcheivedByPlayer = 0;
				// Iterate through all achievements
				for (int i = 0; i < json.length(); i++) {
					JSONObject jsonObject = json.getJSONObject(i);
					// Only add achievement if achieved and count it
					if (jsonObject.getInt("achieved") == 1) {
						achievements.add(new GameAchievement(appId[j],
								jsonObject.getString("apiname"), jsonObject.getString("name")));
						numAcheivedByPlayer++;
					}
				}
				// Calculate completion rate
				completionRate[j] = (numAcheivedByPlayer/numAcheivForGame)*100;
			} catch (JSONException e) {
				logger.warning("Error parsing JSON, likely no achievement data for game ID " + appId[j]);
			}
		}

		return achievements;

	}

	private static String getSteamMediaUrl(int appId, String imageHash) {
		return SteamDataExtractor.STEAM_MEDIA_URL + Integer.toString(appId)
				+ "/" + imageHash + ".jpg";
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
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
			Document doc = builder.parse(is);
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("steamID64");
			Node steamid64Node = nodes.item(0).getFirstChild();
			steamID64 = steamid64Node.getNodeValue();

		} catch (Exception e) {
			return null;
		}
		return steamID64;
	}
}
