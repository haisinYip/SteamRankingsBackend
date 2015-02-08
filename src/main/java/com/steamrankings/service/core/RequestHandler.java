package com.steamrankings.service.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.javalite.activejdbc.Base;
import org.joda.time.DateTime;

import com.steamrankings.service.api.achievements.GameAchievement;
import com.steamrankings.service.api.games.SteamGame;
import com.steamrankings.service.api.leaderboards.RankEntryByAchievements;
import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.service.database.DBConnector;
import com.steamrankings.service.models.Achievement;
import com.steamrankings.service.models.Game;
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.models.ProfilesAchievements;
import com.steamrankings.service.models.ProfilesGames;
import com.steamrankings.service.steam.SteamApi;
import com.steamrankings.service.steam.SteamDataDatabase;
import com.steamrankings.service.steam.SteamDataExtractor;

public class RequestHandler implements Runnable {
    final private String HTTP_REQUEST_GET = "GET";
    final private String REST_API_INTERFACE_PROFILES = "/profile";
    final private static String PARAMETERS_USER_ID = "id";
    final private static String CRLF = "\r\n";

    private Socket socket;
    private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());
    private static final Object PARAMETER_LEADERBOARD_TYPE = "type";
    private static final Object PARAMETER_TO_RANK = "to";
    private static final Object PARAMETER_FROM_RANK = "from";
    private static final Object REST_API_INTERFACE_LEADERBOARDS = "/leaderboards";
    private static final Object REST_API_INTERFACE_GAMES = "/gamesowned";

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
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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
            processGet(restInterface, parameters);
        }
    }

    private void processGet(String restInterface, HashMap<String, String> parameters) throws IOException {
        if (restInterface.equals(REST_API_INTERFACE_PROFILES)) {
            processGetProfiles(parameters);
        } else if (restInterface.equals(REST_API_INTERFACE_LEADERBOARDS)) {
            processGetLeaderboards(parameters);
        } else if (restInterface.equals(REST_API_INTERFACE_GAMES)) {
            processGetGames(parameters);
        }
    }

    private void processGetProfiles(HashMap<String, String> parameters) throws IOException {
        // Check to see if parameters are correct
        if (parameters == null || parameters.isEmpty() || !parameters.containsKey(PARAMETERS_USER_ID)) {
            sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type: " + "text/plain" + CRLF, "Invalid parameters.");
            return;
        }

        long steamId = SteamDataDatabase.convertToSteamId64(parameters.get(PARAMETERS_USER_ID));
        if (steamId == -1) {
            sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type: " + "text/plain" + CRLF, "Invalid steam ID.");
            return;
        }

        SteamApi steamApi = new SteamApi(Application.CONFIG.getProperty("apikey"));
        SteamDataExtractor steamDataExtractor = new SteamDataExtractor(steamApi);

        String serverName = Application.CONFIG.getProperty("server");
        String mport = Application.CONFIG.getProperty("mysql_port");
        String username = Application.CONFIG.getProperty("mysql_username");
        String password = Application.CONFIG.getProperty("mysql_password");

        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://" + serverName + ":" + mport + "/" + "steamrankings_test_db", username, password);
        Profile profile = Profile.findById((int) (steamId - SteamProfile.BASE_ID_64));
        SteamProfile steamProfile = null;
        if (profile == null) {
            steamProfile = steamDataExtractor.getSteamProfile(steamId);
            if (steamProfile == null) {
                sendResponse(socket, "HTTP/1.1 404" + CRLF, "Content-type: " + "text/plain" + CRLF, "A Steam user account with the id" + Long.toString(steamId) + "does not exist.");
                Base.close();
                return;
            } else {
                profile = new Profile();
                profile.set("id", (int) (steamProfile.getSteamId64() - SteamProfile.BASE_ID_64));
                profile.set("community_id", steamProfile.getSteamCommunityId());
                profile.set("persona_name", steamProfile.getPersonaName());
                profile.set("real_name", steamProfile.getRealName());
                profile.set("location_country", steamProfile.getCountryCode());
                profile.set("location_province", steamProfile.getProvinceCode());
                profile.set("location_city", steamProfile.getCityCode());
                profile.set("avatar_full_url", steamProfile.getFullAvatarUrl());
                profile.set("avatar_medium_url", steamProfile.getMediumAvatarUrl());
                profile.set("avatar_icon_url", steamProfile.getIconAvatarUrl());
                System.out.println("JODATIME: " + steamProfile.getLastOnline().getMillis() + " TIMESTAMP: " + new Timestamp((long) steamProfile.getLastOnline().getMillis()).getTime());
                profile.set("last_logoff", new Timestamp(steamProfile.getLastOnline().getMillis()));
                if (profile.insert())
                    System.out.println("Did not save");
                logger.info(profile.toString());
                processNewUser(steamDataExtractor, profile, steamProfile);
            }
        }

        steamProfile = new SteamProfile(profile.getInteger("id") + SteamProfile.BASE_ID_64, profile.getString("community_id"), profile.getString("persona_name"), profile.getString("real_name"),
                profile.getString("location_country"), profile.getString("location_province"), profile.getString("location_citys"), profile.getString("avatar_full_url"),
                profile.getString("avatar_medium_url"), profile.getString("avatar_icon_url"), new DateTime(profile.getTimestamp("last_logoff").getTime()));
        sendResponse(socket, "HTTP/1.1 200" + CRLF, "Content-type: " + "application/json" + CRLF, steamProfile.toString());

        Base.close();
        return;
    }

    private void processNewUser(SteamDataExtractor steamDataExtractor, Profile profile, SteamProfile steamProfile) {
        // Add user's games
        HashMap<SteamGame, Integer> ownedGames = (HashMap<SteamGame, Integer>) steamDataExtractor.getPlayerOwnedGames(steamProfile.getSteamId64());
        for (Entry<SteamGame, Integer> ownedGame : ownedGames.entrySet()) {
            Game game = new Game();
            game.set("id", ownedGame.getKey().getAppId());
            game.set("name", ownedGame.getKey().getName());
            game.set("icon_url", ownedGame.getKey().getIconUrl());
            game.set("logo_url", ownedGame.getKey().getLogoUrl());
            ProfilesGames profilesGames = new ProfilesGames();
            profilesGames.set("profile_id", profile.getId());
            profilesGames.set("game_id", game.getId());
            profilesGames.set("total_play_time", ownedGame.getValue());
            if (ProfilesGames.where("profile_id = ? AND game_id = ?", profile.getId(), game.getId()).isEmpty()) {
                profilesGames.insert();
                logger.info(profilesGames.toString());
            }

            if (Game.findById(ownedGame.getKey().getAppId()) == null) {
                game.insert();
                logger.info(game.toString());
            }

            ArrayList<GameAchievement> gameAchievements = (ArrayList<GameAchievement>) steamDataExtractor.getGameAchievements(ownedGame.getKey().getAppId());

            for (GameAchievement gameAchievement : gameAchievements) {
                Achievement achievement = new Achievement();
                achievement.set("id", gameAchievement.getAchievementId().hashCode());
                achievement.set("game_id", game.getId());
                achievement.set("name", gameAchievement.getName());
                achievement.set("unlocked_icon_url", gameAchievement.getUnlockedIconUrl());
                achievement.set("locked_icon_url", gameAchievement.getLockedIconUrl());
                if (Achievement.where("id = ? AND game_id = ?", gameAchievement.getAchievementId().hashCode(), game.getId()).isEmpty()) {
                    logger.info(achievement.toString());
                    achievement.insert();
                }
            }

            ArrayList<GameAchievement> playerAchievements = (ArrayList<GameAchievement>) steamDataExtractor.getPlayerAchievements(steamProfile.getSteamId64(), ownedGame.getKey().getAppId());

            for (GameAchievement playerAchievement : playerAchievements) {
                ProfilesAchievements achievement = new ProfilesAchievements();
                achievement.set("profile_id", profile.getId());
                achievement.set("achievement_id", playerAchievement.getAchievementId().hashCode());
                achievement.set("game_id", game.getId());
                achievement.set("unlocked_timestamp", new Timestamp(659836800).toString());
                if (ProfilesAchievements.where("id = ? AND profile_id = ? AND game_id = ?", playerAchievement.getAchievementId().hashCode(), profile.getId(), game.getId()).isEmpty()) {
                    logger.info(achievement.toString());
                    achievement.insert();
                }
            }
        }
    }

    private void processGetGames(HashMap<String, String> parameters) throws IOException {
        // Check to see if parameters are correct
        if (parameters == null || parameters.isEmpty()) {
            sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type : " + "text/plain" + CRLF, "Invalid parameters");
            return;
        }

        DBConnector db = new DBConnector();

        if (parameters.containsKey("id")) {
            String sql = "SELECT games_id FROM profiles_has_games WHERE profiles_id=" + Long.toString(Long.parseLong(parameters.get("id")) - SteamProfile.BASE_ID_64);
            ResultSet results = db.queryDB(sql);
            ArrayList<Integer> gameIds = new ArrayList<Integer>();
            try {
                results.first();
                do {
                    gameIds.add(results.getInt(1));
                    results.next();
                } while (!results.isAfterLast());
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            results = db.getTable("games");
            ArrayList<SteamGame> games = new ArrayList<SteamGame>();
            try {
                results.first();
                do {
                    System.out.println(results.getInt("id"));
                    if (gameIds.contains(results.getInt("id"))) {
                        games.add(new SteamGame(results.getInt("id"), results.getString("icon_url"), results.getString("logo_url"), results.getString("name")));
                    }
                    results.next();
                } while (!results.isAfterLast());
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            db.closeConnection();
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(games);
            // JSONArray gamesJson = new JSONArray(games);
            sendResponse(socket, "HTTP/1.1 200" + CRLF, "Content-type: " + "application/json" + CRLF, json);
        } else if (parameters.containsKey("appId")) {

        } else {

        }
    }

    private void processGetLeaderboards(HashMap<String, String> parameters) throws IOException {
        // Check to see if parameters are correct
        if (parameters == null || parameters.isEmpty() || !parameters.containsKey(PARAMETER_LEADERBOARD_TYPE) || !parameters.containsKey(PARAMETER_TO_RANK)
                || !parameters.containsKey(PARAMETER_FROM_RANK)) {
            sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type : " + "text/plain" + CRLF, "Invalid parameters");
            return;
        }

        if (parameters.get(PARAMETER_LEADERBOARD_TYPE).equals("achievements")) {
            DBConnector db = new DBConnector();
            ArrayList<RankEntryByAchievements> leaderboard = processGetAchievementLeaderboard(db, parameters.get(PARAMETER_TO_RANK), parameters.get(PARAMETER_FROM_RANK));
            if (leaderboard == null) {
                sendResponse(socket, "HTTP/1.1 400" + CRLF, "Content-type: " + "text/plain" + CRLF, "Something went wrong");
                db.closeConnection();
                return;
            } else {
                sendResponse(socket, "HTTP/1.1 200" + CRLF, "Content-type: " + "application/json" + CRLF, leaderboard.toString());
                db.closeConnection();
                return;
            }
        }
    }

    private ArrayList<RankEntryByAchievements> processGetAchievementLeaderboard(DBConnector db, String string, String string2) {
        return null;
    }

    private void sendResponse(Socket socket, String statusLine, String contentTypeLine, String entity) throws IOException {
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        output.writeBytes(statusLine);
        output.writeBytes(contentTypeLine);
        output.writeBytes(CRLF);
        output.writeBytes(entity);

        output.close();
    }
}