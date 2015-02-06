package com.steamrankings.service.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.koraktor.steamcondenser.steam.community.SteamId;
import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.service.core.dataextractors.SteamDataExtractor;
import com.steamrankings.service.database.DBConnector;

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

        SteamId steamId = SteamDataExtractor.convertToSteamId64(parameters.get(PARAMETERS_USER_ID));
        if (steamId == null) {
            // send HTTP error
        }

        DBConnector db = new DBConnector();
        ArrayList<Integer> userIds = new ArrayList<Integer>();
        userIds.add((int) (steamId.getSteamId64() - SteamProfile.BASE_ID_64));

        ArrayList<SteamProfile> profiles = (ArrayList<SteamProfile>) SteamDataExtractor.getProfileFromDatabase(userIds, db);

        // Not in database, must get it from Steam API and add it to the database
        if (profiles == null || profiles.isEmpty()) {
            profiles = (ArrayList<SteamProfile>) SteamDataExtractor.extractProfileFromSteam(userIds, db);
            processNewUser(steamId, db);
        }

        // Return the profile
    }
    
    private void processNewUser(SteamId user, DBConnector db) {
        // Add user's games
        // Add all the games' achievements
        // Add the achievements the user unlocked
        SteamDataExtractor.extractAndAddGamesToDatabase(user, db);
        // Add the user's friends
    }
}