package com.steamrankings.service.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
    final private static String CRLF = "\r\n";

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

    private void processGet(String restInterface, HashMap<String, String> parameters) throws IOException {
        if (restInterface.equals(REST_API_INTERFACE_PROFILES)) {
            processGetProfiles(parameters);
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

        DBConnector db = new DBConnector();
        SteamApi steamApi = new SteamApi(Application.CONFIG.getProperty("apikey"));
        SteamDataExtractor steamDataExtractor = new SteamDataExtractor(steamApi);

        SteamProfile profile = SteamDataDatabase.getProfileFromDatabase((int) (steamId - SteamProfile.BASE_ID_64), db);

        // Not in database, must get it from Steam API and add it to the
        // database
        if (profile == null) {
            profile = steamDataExtractor.getSteamProfile(steamId);
            if (profile == null) {
                sendResponse(socket, "HTTP/1.1 404" + CRLF, "Content-type: " + "text/plain" + CRLF, "A Steam user account with the id" + Long.toString(steamId) + "does not exist.");
                db.closeConnection();
                return;
            } else {
                SteamDataDatabase.addProfileToDatabase(db, profile);
                processNewUser(db, steamDataExtractor, profile);
            }
        }

        // Return the profile
        sendResponse(socket, "HTTP/1.1 200" + CRLF, "Content-type: " + "application/json" + CRLF, profile.toString());
        db.closeConnection();

        return;
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

    private void sendResponse(Socket socket, String statusLine, String contentTypeLine, String entity) throws IOException {
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        output.writeBytes(statusLine);
        output.writeBytes(contentTypeLine);
        output.writeBytes(CRLF);
        output.writeBytes(entity);

        output.close();
    }
}