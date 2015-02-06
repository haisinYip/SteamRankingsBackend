package com.steamrankings.service.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.steamrankings.service.api.achievements.GameAchievement;
import com.steamrankings.service.api.games.SteamGame;
import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.service.database.DBConnector;
import com.steamrankings.service.steam.SteamApi;
import com.steamrankings.service.steam.SteamDataDatabase;
import com.steamrankings.service.steam.SteamDataExtractor;

public class RequestHandler implements Runnable {
    final private String HTTP_REQUEST_GET = "GET";
    final private String REST_API_INTERFACE_PROFILES = "/profile";
    final private static String PARAMETERS_USER_ID = "id";

    private Socket socket;
    private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());

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

    private void processGet(String restInterface, HashMap<String, String> parameters) {
        if (restInterface.equals(REST_API_INTERFACE_PROFILES)) {
            processGetProfiles(parameters);
        }
    }

    private void processGetProfiles(HashMap<String, String> parameters) {
        // Check to see if parameters are correct
        if (parameters == null || parameters.isEmpty() || !parameters.containsKey(PARAMETERS_USER_ID)) {
            // send HTTP error
        }

        long steamId = SteamDataDatabase.convertToSteamId64(parameters.get(PARAMETERS_USER_ID));
        if (steamId == -1) {
            // send HTTP error
        }

        DBConnector db = new DBConnector();
        SteamApi steamApi = new SteamApi(Application.CONFIG.getProperty("apikey"));
        SteamDataExtractor steamDataExtractor = new SteamDataExtractor(steamApi);

        SteamProfile profile = SteamDataDatabase.getProfileFromDatabase((int) (steamId - SteamProfile.BASE_ID_64), db);

        // Not in database, must get it from Steam API and add it to the
        // database
        if (profile == null) {
            profile = steamDataExtractor.getSteamProfile(steamId);
            if (profile == null) {
                // User does not exist
                // Return error
            } else {
                SteamDataDatabase.addProfileToDatabase(db, profile);
                processNewUser(db, steamDataExtractor, profile);
            }
        }

        // Return the profile
        // return profile
    }

    private void processNewUser(DBConnector db, SteamDataExtractor steamDataExtractor, SteamProfile profile) {
        // Add user's games
        HashMap<SteamGame, Integer> ownedGames = (HashMap<SteamGame, Integer>) steamDataExtractor.getPlayerOwnedGames(profile.getSteamId64());
        SteamDataDatabase.addGamesToDatabase(db, profile, ownedGames);

        // Add all the games' achievements
        // Add the achievements the user unlocked
        for (Entry<SteamGame, Integer> ownedGame : ownedGames.entrySet()) {
            ArrayList<GameAchievement> gameAchievements = (ArrayList<GameAchievement>) steamDataExtractor.getGameAchievements(ownedGame.getKey().getAppId());
            ArrayList<GameAchievement> playerAchievements = (ArrayList<GameAchievement>) steamDataExtractor.getPlayerAchievements(profile.getSteamId64(), ownedGame.getKey().getAppId());
            SteamDataDatabase.addAchievementsToDatabase(db, profile, ownedGame.getKey(), gameAchievements, playerAchievements);
        }

        // Add the user's friends
    }
}