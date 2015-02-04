package com.steamrankings.service.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;

import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.service.core.dataextractors.ProfileDataExtractor;
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

        long id = ProfileDataExtractor.convertToSteamId64(parameters.get(PARAMETERS_USER_ID));
        if (id == -1) {
            // send HTTP error
        }

        DBConnector db = new DBConnector();
        String[] columns = { "id", "total_play_time", "steam_id_64", "steam_community_id", "persona_name", "real_name", "country_code", "avatar_full_url", "avatar_medium_url", "avatar_icon_url" };
        ResultSet dbResults = db.readData("profiles", columns);
        try {
            dbResults.first();
        } catch (SQLException e) {
            // send HTTP error
        }

        SteamProfile profile = ProfileDataExtractor.getSteamProfile(id, dbResults);

        if (profile != null) {
            String[][] data = { { "0", "0", Long.toString(profile.getSteamId64()), profile.getSteamCommunityId(), profile.getPersonaName(), profile.getRealName(), "CA", profile.getFullAvatar(), profile.getMediumAvatar(), profile.getIconAvatar() } };
            db.writeData("profiles", data);
            // answer HTTP request with JSON data
        } else {
            // send HTTP error
        }
    }
}
