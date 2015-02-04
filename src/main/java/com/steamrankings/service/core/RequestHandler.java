package com.steamrankings.service.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
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

        long id = SteamDataExtractor.convertToSteamId64(parameters.get(PARAMETERS_USER_ID));
        if (id == -1) {
            // send HTTP error
        }

        DBConnector db = new DBConnector();
        String[] columns = { "id", "total_play_time", "steam_id_64", "steam_community_id", "persona_name", "real_name", "country_code", "avatar_full_url", "avatar_medium_url", "avatar_icon_url" };
        ResultSet dbResults = db.readData("profiles", columns);
        
        SteamProfile profile = null;
        
        /////////////////////////////////////////
        // Needs to be refactored into a DBConnector function
        /////////////////////////////////////////
        
        try {
            dbResults.first();
            while (!dbResults.isAfterLast()) {
                if (dbResults.getLong("steam_id_64") == id) {
                    break;
                } else {
                    dbResults.next();
                }
            }

            if (!dbResults.isAfterLast()) {
                profile = new SteamProfile(dbResults.getLong("steam_id_64"), dbResults.getString("steam_community_id"), dbResults.getString("persona_name"),
                        dbResults.getString("real_name"), dbResults.getString("country_code"), dbResults.getString("avatar_full_url"), dbResults.getString("avatar_medium_url"),
                        dbResults.getString("avatar_icon_url"), new DateTime(0));
            }
            
        } catch (SQLException e) {
         // send HTTP error
        }
        
        //////////////////////////////////////////
        //////////////////////////////////////////

        if (profile != null) {
            // profile in database
            // answer HTTP request with JSON data
        } else {
            // Profile not in database
            
            SteamId steamId = SteamDataExtractor.getSteamId(id);

            // Steam profile exists
            if (steamId != null) {
                ArrayList<SteamId> steamIds = new ArrayList<SteamId>();
                steamIds.add(steamId);
                
                try {
                    SteamId[] friendIds = steamId.getFriends();
                    for(SteamId friendId : friendIds) {
                        friendId = SteamDataExtractor.getSteamId(friendId.getSteamId64());
                        if(friendId != null) {
                            steamIds.add(friendId);
                        }
                    }
                } catch (SteamCondenserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                SteamDataExtractor dataExtractor = new SteamDataExtractor(steamIds, new DBConnector());
                dataExtractor.addUsers();
                //dataExtractor.addGames();
                //dataExtractor.addGameAchievements();
                //dataExtractor.addUserAchievements();
            }
            
            /*
            // Write profile to database
            if (profile != null) {
                String[][] data = { { "0", "0", Long.toString(profile.getSteamId64()), profile.getSteamCommunityId(), profile.getPersonaName(), profile.getRealName(), "CA", profile.getFullAvatar(),
                        profile.getMediumAvatar(), profile.getIconAvatar() } };
                db.writeData("profiles", data);
                // answer HTTP request with JSON data
            }
            */
        }
    }
}