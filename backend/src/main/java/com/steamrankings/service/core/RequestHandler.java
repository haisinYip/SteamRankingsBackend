package com.steamrankings.service.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.joda.time.DateTime;

import com.steamrankings.service.api.achievements.GameAchievement;
import com.steamrankings.service.api.games.SteamGame;
import com.steamrankings.service.api.leaderboards.RankEntryByAchievements;
import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.service.models.Achievement;
import com.steamrankings.service.models.Game;
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.models.ProfilesAchievements;
import com.steamrankings.service.models.ProfilesGames;
import com.steamrankings.service.steam.SteamApi;
import com.steamrankings.service.steam.SteamDataExtractor;

public class RequestHandler implements Runnable {
	private static final Logger logger = Logger.getLogger(RequestHandler.class
			.getName());

	private static final String CRLF = "\r\n";
	private static final String HTTP_REQUEST_GET = "GET";
	private static final String REST_API_INTERFACE_PROFILES = "/profile";
	private static final String REST_API_INTERFACE_LEADERBOARDS = "/leaderboards";
	private static final String REST_API_INTERFACE_GAMES = "/games";
	private static final String REST_API_INTERFACE_ACHIEVEMENTS = "/achievements";

	private static final String PARAMETERS_USER_ID = "id";
	private static final String PARAMETERS_APP_ID = "appId";
	private static final String PARAMETER_LEADERBOARD_TYPE = "type";
	private static final String PARAMETER_TO_RANK = "to";
	private static final String PARAMETER_FROM_RANK = "from";

	private static final String API_ERROR_BAD_ARGUMENTS_CODE = "1000";
	private static final String API_ERROR_STEAM_USER_DOES_NOT_EXIST = "2000";
	private static final String API_ERROR_STEAM_ID_INVALID = "3000";

	private static final int AVG_NUM_GAMES_NOT_IN_DB = 50;
	private Socket socket;

	public RequestHandler(Socket socket) throws Exception {
		this.socket = socket;
	}

	public void run() {
		try {
			processRequest();
		} catch (IOException e) {
			logger.log(Level.FINE, "Error processing the request." + e);
		}
	}

	private void processRequest() throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));

		// Read the first header line that includes the HTTP request
		String requestLine = input.readLine();

		StringTokenizer tokens = new StringTokenizer(requestLine);

		// HTTP request type
		String httpRequestType = tokens.nextToken();

		requestLine = tokens.nextToken();
		tokens = new StringTokenizer(requestLine, "?");

		String restInterface = tokens.nextToken();
		requestLine = tokens.nextToken();

		tokens = new StringTokenizer(requestLine, "&");
		HashMap<String, String> parameters = new HashMap<String, String>();
		while (tokens.hasMoreTokens()) {
			String[] params = tokens.nextToken().split("=");
			parameters.put(params[0], params[1]);
		}

		if (httpRequestType.equals(HTTP_REQUEST_GET)) {
			openDBConnection();
			processGet(restInterface, parameters);

			closeDBConnection();
		}
	}

	private void openDBConnection() {
		// Base.open("com.mysql.jdbc.Driver",
		// "jdbc:mysql://" + Application.CONFIG.getProperty("server")
		// + ":" + Application.CONFIG.getProperty("mysql_port")
		// + "/" + "steamrankings_test_db"
		// + "?characterEncoding=utf8",
		// Application.CONFIG.getProperty("mysql_username"),
		// Application.CONFIG.getProperty("mysql_password"));
		Base.open("com.mysql.jdbc.Driver",
				"jdbc:mysql://" + Application.CONFIG.getProperty("server")
						+ ":" + Application.CONFIG.getProperty("mysql_port")
						+ "/" + "steamrankings_test_db"
						+ "?characterEncoding=utf8",
				Application.CONFIG.getProperty("mysql_username"),
				Application.CONFIG.getProperty("mysql_password"));

	}

	private void closeDBConnection() {
		Base.close();
	}

	private void processGet(String restInterface,
			HashMap<String, String> parameters) throws IOException {
		if (restInterface.equals(REST_API_INTERFACE_PROFILES)) {
			processGetProfiles(parameters);
		} else if (restInterface.equals(REST_API_INTERFACE_LEADERBOARDS)) {
			processGetLeaderboards(parameters);
		} else if (restInterface.equals(REST_API_INTERFACE_GAMES)) {
			processGetGames(parameters);
		} else if (restInterface.equals(REST_API_INTERFACE_ACHIEVEMENTS)) {
			processGetAchievements(parameters);
		}
	}

	private void processGetProfiles(HashMap<String, String> parameters)
			throws IOException {
		// Check to see if parameters are correct
		if (parameters == null || parameters.isEmpty()
				|| !parameters.containsKey(PARAMETERS_USER_ID)) {
			sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type: "
					+ "text/plain" + CRLF, API_ERROR_BAD_ARGUMENTS_CODE);
			return;
		}

		long steamId = SteamDataExtractor.convertToSteamId64(parameters
				.get(PARAMETERS_USER_ID));
		if (steamId == -1) {
			sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type: "
					+ "text/plain" + CRLF, API_ERROR_STEAM_ID_INVALID);
			return;
		}

		SteamApi steamApi = new SteamApi(
				Application.CONFIG.getProperty("apikey"));
		SteamDataExtractor steamDataExtractor = new SteamDataExtractor(steamApi);

		Profile profile = Profile
				.findById((int) (steamId - SteamProfile.BASE_ID_64));
		SteamProfile steamProfile = null;

		if (profile == null) {
			steamProfile = steamDataExtractor.getSteamProfile(steamId);
			if (steamProfile == null) {
				sendResponse(socket, "HTTP/1.1 404" + CRLF, "Content-type: "
						+ "text/plain" + CRLF,
						API_ERROR_STEAM_USER_DOES_NOT_EXIST);
				return;
			} else {

				profile = new Profile();
				profile.set(
						"id",
						(int) (steamProfile.getSteamId64() - SteamProfile.BASE_ID_64));
				profile.set("community_id", steamProfile.getSteamCommunityId());
				profile.set("persona_name", steamProfile.getPersonaName());
				profile.set("real_name", steamProfile.getRealName());
				profile.set("location_country", steamProfile.getCountryCode());
				profile.set("location_province", steamProfile.getProvinceCode());
				profile.set("location_city", steamProfile.getCityCode());
				profile.set("avatar_full_url", steamProfile.getFullAvatarUrl());
				profile.set("avatar_medium_url",
						steamProfile.getMediumAvatarUrl());
				profile.set("avatar_icon_url", steamProfile.getIconAvatarUrl());

				profile.set("last_logoff", new Timestamp(steamProfile
						.getLastOnline().getMillis()));
				profile.insert();

				logger.info(profile.toString());
				processNewUser(steamDataExtractor, profile, steamProfile);
			}
		}

		steamProfile = new SteamProfile(profile.getInteger("id")
				+ SteamProfile.BASE_ID_64, profile.getString("community_id"),
				profile.getString("persona_name"),
				profile.getString("real_name"),
				profile.getString("location_country"),
				profile.getString("location_province"),
				profile.getString("location_citys"),
				profile.getString("avatar_full_url"),
				profile.getString("avatar_medium_url"),
				profile.getString("avatar_icon_url"), new DateTime(profile
						.getTimestamp("last_logoff").getTime()));
		sendResponseUTF(socket, "HTTP/1.1 200" + CRLF, "Content-type: "
				+ "application/json ; charset=UTF-8" + CRLF,
				steamProfile.toString());

		return;
	}

	private void processNewUser(SteamDataExtractor steamDataExtractor,
			Profile profile, SteamProfile steamProfile) {

		long time = System.currentTimeMillis();
		// Get user game list
		HashMap<SteamGame, Integer> ownedGames = (HashMap<SteamGame, Integer>) steamDataExtractor
				.getPlayerOwnedGames(steamProfile.getSteamId64());

		// Get list of all games + achievements in DB, convert to array of IDs
		LazyList<Game> gamesDB = Game.findAll();

		ArrayList<Long> idListDB = new ArrayList<>(gamesDB.size());
		for (Game game : gamesDB) {
			idListDB.add(game.getLongId());
		}

		// Go through all games owned by player, check for missing ones with DB
		ArrayList<SteamGame> notContain = new ArrayList<>(
				AVG_NUM_GAMES_NOT_IN_DB);
		for (SteamGame game : ownedGames.keySet()) {
			if (!idListDB.contains((long) game.getAppId())) {
				notContain.add(game);
			}
		}

		// Define list of IDs from not games not in DB
		int[] idListNotContain = new int[notContain.size()];

		// Add all missing games to DB, create ID list for missing games at the
		// same time
		PreparedStatement ps = Base
				.startBatch("insert into games (id, name, icon_url, logo_url) values(?, ?, ?, ?)");

		int i = 0;
		for (SteamGame game : notContain) {
			Base.addBatch(ps, game.getAppId(), game.getName(),
					game.getIconUrl(), game.getLogoUrl());
			idListNotContain[i++] = game.getAppId();
		}

		Base.executeBatch(ps);

		// Get all achievements for all missing games
		ArrayList<GameAchievement> gameAchievements = (ArrayList<GameAchievement>) steamDataExtractor
				.getGameAchievementsThreaded(idListNotContain);

		// Add all missing achievements to DB
		ps = Base
				.startBatch("insert into achievements (id, game_id, name, description, unlocked_icon_url, locked_icon_url) values (?,?,?,?,?,?)");
		for (GameAchievement achievement : gameAchievements) {
			// Note achievement hash is the ID ("apiname" in JSON, e.g.
			// TF_PLAY_GAME_EVERYCLASS) + the human readable name (e.g. Head of
			// the Class)
			// to ensure enough variation for hashing. Some apinames are only
			// 2-3 characters long which leads to collisions
			Base.addBatch(ps, (achievement.getAchievementId() + achievement.getName()).hashCode(),
					achievement.getAppId(), achievement.getName(),
					achievement.getDescription(),
					achievement.getUnlockedIconUrl(),
					achievement.getLockedIconUrl());
		}

		Base.executeBatch(ps);

		// Clear gameAcheivement list to save memory
		gameAchievements.clear();
		gameAchievements.trimToSize();

		// Add links from profile to achievements

		// First create array of IDs
		int[] ownedGamesIdList = new int[ownedGames.size()];
		i = 0;
		for (SteamGame game : ownedGames.keySet()) {
			ownedGamesIdList[i++] = game.getAppId();
		}

		// Create array for completion rate
		float[] completionRate = new float[ownedGames.size()];

		// Call Steam API to get all achievements for all games player owns
		ArrayList<GameAchievement> playerAchievements = (ArrayList<GameAchievement>) steamDataExtractor
				.getPlayerAchievementsThreaded(steamProfile.getSteamId64(),
						ownedGamesIdList, completionRate);

		// Add profile -> achievement links to DB
		ps = Base
				.startBatch("insert into profiles_achievements (profile_id, achievement_id, game_id, unlocked_timestamp) values (?,?,?,?)");
		for (GameAchievement achievement : playerAchievements) {
			// TODO: Timestamp is a placeholder
			Base.addBatch(ps, profile.getId(),
					(achievement.getAchievementId() + achievement.getName()).hashCode(), achievement.getAppId(),
					new Timestamp(659836800).toString());
		}

		Base.executeBatch(ps);

		// Add all links from profile to each game, also calculate completion
		// ratio
		ps = Base
				.startBatch("insert into profiles_games (profile_id, game_id, total_play_time, completion_rate) values (?,?,?,?)");
		i = 0;
		for (Entry<SteamGame, Integer> ownedGame : ownedGames.entrySet()) {
			Base.addBatch(ps, profile.getId(), ownedGame.getKey().getAppId(),
					ownedGame.getValue(), completionRate[i++]);
		}
		Base.executeBatch(ps);

		float avgCompletionRate = 0;
		if (completionRate.length != 0) {
			avgCompletionRate = mean(completionRate);
		}
		profile.setFloat("avg_completion_rate", avgCompletionRate);
		profile.saveIt();

		// Close prepared statement because we're done with it
		try {
			ps.close();
		} catch (SQLException e) {
			logger.warning("Unable to close prepared statement in batch processing");
			e.printStackTrace();
		}

		System.out.println("Time taken to add new user: "
				+ (System.currentTimeMillis() - time));

	}

	private void processGetGames(HashMap<String, String> parameters)
			throws IOException {
		// Check to see if parameters are correct
		if (parameters == null || parameters.isEmpty()) {
			sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type : "
					+ "text/plain" + CRLF, API_ERROR_BAD_ARGUMENTS_CODE);
			return;
		}

		long steamId = SteamDataExtractor.convertToSteamId64(parameters
				.get("id"));

		if (parameters.containsKey(PARAMETERS_USER_ID)) {
			List<ProfilesGames> list = ProfilesGames
					.where("profile_id = ?",
							(int) (steamId - SteamProfile.BASE_ID_64))
					.orderBy("total_play_time desc").limit(30);
			ArrayList<ProfilesGames> profilesGames = new ArrayList<ProfilesGames>(
					list);
			if (profilesGames != null) {
				ArrayList<SteamGame> steamGames = new ArrayList<SteamGame>();
				for (ProfilesGames profilesGame : profilesGames) {
					Game game = Game.findById(profilesGame.get("game_id"));
					if (game != null) {
						steamGames.add(new SteamGame(game.getInteger("id"),
								game.getString("icon_url"), game
										.getString("logo_url"), game
										.getString("name")));
					}
				}
				ObjectMapper mapper = new ObjectMapper();
				sendResponseUTF(socket, "HTTP/1.1 200" + CRLF,
						"Content-type : " + "application/json ; charset=UTF-8"
								+ CRLF, mapper.writeValueAsString(steamGames));
				return;
			}
		} else {
			List<Game> list = Game.findAll();
			ArrayList<Game> games = new ArrayList<Game>(list);
			if (games != null) {
				ArrayList<SteamGame> steamGames = new ArrayList<SteamGame>();
				for (Game game : games) {
					steamGames.add(new SteamGame(game.getInteger("id"), game
							.getString("icon_url"), game.getString("logo_url"),
							game.getString("name")));
				}
				ObjectMapper mapper = new ObjectMapper();
				sendResponseUTF(socket, "HTTP/1.1 200" + CRLF,
						"Content-type : " + "application/json ; charset=UTF-8"
								+ CRLF, mapper.writeValueAsString(steamGames));
				return;
			}
		}
	}

	private void processGetAchievements(HashMap<String, String> parameters)
			throws IOException {
		// Check to see if parameters are correct
		if (parameters == null || parameters.isEmpty()) {
			sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type : "
					+ "text/plain" + CRLF, API_ERROR_BAD_ARGUMENTS_CODE);
			return;
		}

		long steamId = SteamDataExtractor.convertToSteamId64(parameters
				.get("id"));

		if (parameters.containsKey(PARAMETERS_USER_ID)
				&& parameters.containsKey(PARAMETERS_APP_ID)) {
			List<ProfilesAchievements> list = ProfilesAchievements.where(
					"profile_id = ? AND game_id = ?",
					(int) (steamId - SteamProfile.BASE_ID_64),
					Integer.parseInt(parameters.get(PARAMETERS_APP_ID))).limit(
					15);
			ArrayList<ProfilesAchievements> profilesAchievements = new ArrayList<ProfilesAchievements>(
					list);
			if (profilesAchievements != null) {
				ArrayList<GameAchievement> gameAchievements = new ArrayList<GameAchievement>();
				for (ProfilesAchievements profilesAchievement : profilesAchievements) {
					Achievement achievement = Achievement.findFirst(
							"id = ? AND game_id = ?",
							profilesAchievement.getInteger("achievement_id"),
							profilesAchievement.getInteger("game_id"));
					if (achievement != null) {
						gameAchievements.add(new GameAchievement(achievement
								.getInteger("game_id"), achievement
								.getString("id"),
								achievement.getString("name"), achievement
										.getString("description"), achievement
										.getString("unlocked_icon_url"),
								achievement.getString("locked_icon_url"),
								new DateTime(profilesAchievement.getTimestamp(
										"unlocked_timestamp").getTime())));
					}
				}
				ObjectMapper mapper = new ObjectMapper();
				sendResponseUTF(socket, "HTTP/1.1 200" + CRLF,
						"Content-type : " + "application/json ; charset=UTF-8"
								+ CRLF,
						mapper.writeValueAsString(gameAchievements));
				return;
			}
		} else if (parameters.containsKey(PARAMETERS_USER_ID)) {
			List<ProfilesAchievements> list = ProfilesAchievements
					.where("profile_id = ?",
							(int) (steamId - SteamProfile.BASE_ID_64))
					.limit(30);
			ArrayList<ProfilesAchievements> profilesAchievements = new ArrayList<ProfilesAchievements>(
					list);
			if (profilesAchievements != null) {
				ArrayList<GameAchievement> gameAchievements = new ArrayList<GameAchievement>();
				for (ProfilesAchievements profilesAchievement : profilesAchievements) {
					Achievement achievement = Achievement.findFirst(
							"id = ? AND game_id = ?",
							profilesAchievement.getInteger("achievement_id"),
							profilesAchievement.getInteger("game_id"));
					if (achievement != null) {
						gameAchievements.add(new GameAchievement(achievement
								.getInteger("game_id"), achievement
								.getString("id"),
								achievement.getString("name"), achievement
										.getString("description"), achievement
										.getString("unlocked_icon_url"),
								achievement.getString("locked_icon_url"),
								new DateTime(profilesAchievement.getTimestamp(
										"unlocked_timestamp").getTime())));
					}
				}
				ObjectMapper mapper = new ObjectMapper();
				sendResponseUTF(socket, "HTTP/1.1 200" + CRLF,
						"Content-type : " + "application/json ; charset=UTF-8"
								+ CRLF,
						mapper.writeValueAsString(gameAchievements));
				return;
			}
		} else if (parameters.containsKey(PARAMETERS_APP_ID)) {
			List<Achievement> list = ProfilesAchievements.where("game_id = ?",
					Integer.parseInt(parameters.get(PARAMETERS_APP_ID)));
			ArrayList<Achievement> achievements = new ArrayList<Achievement>(
					list);
			ArrayList<GameAchievement> gameAchievements = new ArrayList<GameAchievement>();
			for (Achievement achievement : achievements) {
				gameAchievements.add(new GameAchievement(achievement
						.getInteger("game_id"), achievement.getString("id"),
						achievement.getString("name"), achievement
								.getString("description"), achievement
								.getString("unlocked_icon_url"), achievement
								.getString("locked_icon_url")));
			}
			ObjectMapper mapper = new ObjectMapper();
			sendResponseUTF(socket, "HTTP/1.1 200" + CRLF, "Content-type : "
					+ "application/json ; charset=UTF-8" + CRLF,
					mapper.writeValueAsString(gameAchievements));
			return;
		} else {
			sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type : "
					+ "text/plain" + CRLF, API_ERROR_BAD_ARGUMENTS_CODE);
		}
	}

	private void processGetLeaderboards(HashMap<String, String> parameters)
			throws IOException {
		// Check to see if parameters are correct
		if (parameters == null || parameters.isEmpty()
				|| !parameters.containsKey(PARAMETER_LEADERBOARD_TYPE)
				|| !parameters.containsKey(PARAMETER_TO_RANK)
				|| !parameters.containsKey(PARAMETER_FROM_RANK)) {
			sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type : "
					+ "text/plain" + CRLF, "Invalid parameters");
			return;
		}

		if (parameters.get(PARAMETER_LEADERBOARD_TYPE).equals("achievements")) {
			ArrayList<RankEntryByAchievements> leaderboard = processGetAchievementLeaderboard(
					parameters.get(PARAMETER_TO_RANK),
					parameters.get(PARAMETER_FROM_RANK));
			if (leaderboard == null) {
				sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type: "
						+ "text/plain" + CRLF, "Something went wrong");
				return;
			} else {
				sendResponseUTF(socket, "HTTP/1.1 200" + CRLF,
						"Content-type : " + "application/json ; charset=UTF-8"
								+ CRLF, leaderboard.toString());
				return;
			}
		}

		sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type : "
				+ "text/plain" + CRLF, API_ERROR_BAD_ARGUMENTS_CODE);
	}

	private ArrayList<RankEntryByAchievements> processGetAchievementLeaderboard(
			String toRank, String fromRank) {
		int from = Integer.parseInt(fromRank);
		int to = Integer.parseInt(toRank);
		if (from > to) {
			return null;
		}

		List<Profile> listProfiles = Profile.findAll();
		ArrayList<Profile> profiles = new ArrayList<Profile>(listProfiles);
		HashMap<Profile, Integer> profileAchievementCounts = new HashMap<Profile, Integer>();
		for (Profile profile : profiles) {
			profileAchievementCounts.put(
					profile,
					ProfilesAchievements.where("profile_id = ?",
							profile.getInteger("id")).size());
		}

		int i = 1;
		ArrayList<RankEntryByAchievements> rankEntries = new ArrayList<RankEntryByAchievements>();
		for (Entry<Profile, Integer> profileAchievementCount : profileAchievementCounts
				.entrySet()) {
			rankEntries.add(new RankEntryByAchievements(i,
					profileAchievementCount.getKey().getInteger("id")
							+ SteamProfile.BASE_ID_64, profileAchievementCount
							.getKey().getString("persona_name"),
					profileAchievementCount.getValue(), profileAchievementCount
							.getKey().getFloat("avg_completion_rate")
							.toString() + '%', profileAchievementCount.getKey()
							.getString("location_country")));
		}
		Collections.sort(rankEntries,
				new Comparator<RankEntryByAchievements>() {
					public int compare(RankEntryByAchievements o1,
							RankEntryByAchievements o2) {
						return o2.getAchievementsTotal()
								- o1.getAchievementsTotal();
					}
				});
		for (RankEntryByAchievements rank : rankEntries) {
			rankEntries.get(i - 1).setRankNumber(i);
			i++;
		}
		return rankEntries;
	}

	private void sendResponse(Socket socket, String statusLine,
			String contentTypeLine, String entity) throws IOException {
		DataOutputStream output = new DataOutputStream(socket.getOutputStream());

		output.writeBytes(statusLine);
		output.writeBytes(contentTypeLine);
		output.writeBytes(CRLF);
		output.writeBytes(entity);

		output.close();
	}

	private void sendResponseUTF(Socket socket, String statusLine,
			String contentTypeLine, String entity) throws IOException {
		DataOutputStream output = new DataOutputStream(socket.getOutputStream());

		output.writeBytes(statusLine);
		output.writeBytes(contentTypeLine);
		output.writeBytes(CRLF);
		byte[] entityBytes = entity.getBytes("UTF-8");
		output.write(entityBytes, 0, entityBytes.length);

		output.close();
	}

	public static float mean(float[] p) {

		float sum = 0;

		for (int i = 0; i < p.length; i++) {
			sum += p[i];
		}
		return sum / p.length;
	}
}