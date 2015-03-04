/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steamrankings.service.core;

import com.steamrankings.service.api.ErrorCodes;
import com.steamrankings.service.api.profiles.SteamProfile;
import static com.steamrankings.service.core.ProfileHandler.PARAMETERS_USER_ID;
import static com.steamrankings.service.core.ResponseHandler.sendData;
import static com.steamrankings.service.core.ResponseHandler.sendError;
import com.steamrankings.service.database.Database;
import com.steamrankings.service.models.Blacklist;
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.steam.SteamApi;
import com.steamrankings.service.steam.SteamDataExtractor;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class BlacklistHandler extends AbstractHandler {

    private final static Logger LOGGER = Logger.getLogger(BlacklistHandler.class.getName());

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // Open DB connection
        Database.openDBConnection();

        Map<String, String[]> param = request.getParameterMap();

        System.out.println("Blacklist method began");
        if (param == null || param.isEmpty() || !param.containsKey(PARAMETERS_USER_ID)) {
            sendError(ErrorCodes.API_ERROR_BAD_ARGUMENTS, response, baseRequest);
            Database.closeDBConnection();
            return;
        }

        long steamId = -1;
        if (param.containsKey(PARAMETERS_USER_ID)) {
            steamId = SteamDataExtractor.convertToSteamId64(param.get(PARAMETERS_USER_ID)[0]);
            if (steamId == -1) {
                sendError(ErrorCodes.API_ERROR_STEAM_ID_INVALID, response, baseRequest);
                Database.closeDBConnection();
                return;
            }
        }

        // add id to the blacklist table if it exists and show an error if it
        // doesn't exist
        SteamApi steamApi = new SteamApi(Initialization.CONFIG.getProperty("apikey"));
        SteamDataExtractor steamDataExtractor = new SteamDataExtractor(steamApi);
        SteamProfile steamProfile = steamDataExtractor.getSteamProfile(steamId);

        if (steamProfile == null) {
            sendError(ErrorCodes.API_ERROR_STEAM_USER_DOES_NOT_EXIST, response, baseRequest);
            Database.closeDBConnection();
            return;
        }

        // remove profile from the database if it exists
        // everytime you add new user we have to check in a function that the
        // user doesn't exist in the blacklist
        Blacklist blacklist = Blacklist.findById(steamId - SteamProfile.BASE_ID_64);

        if (!isUserInBlackList(steamId)) {
            blacklist = new Blacklist();
            blacklist.set("id", (int) (steamId - SteamProfile.BASE_ID_64));
            blacklist.insert();
        }
        Profile profile = Profile.findById((int) (steamId - SteamProfile.BASE_ID_64));

        if (profile != null) {
            profile.deleteCascadeShallow();
            System.out.println("Blacklist method is done");
        }
        sendData(steamProfile.toString(), response, baseRequest);
        Database.closeDBConnection();
    }

    public static boolean isUserInBlackList(long steamId64) {
        Blacklist blackListedUser = Blacklist.findById((int) (steamId64 - SteamProfile.BASE_ID_64));
        return blackListedUser != null;
    }
}
