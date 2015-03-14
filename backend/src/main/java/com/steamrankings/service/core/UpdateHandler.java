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
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.steam.SteamApi;
import com.steamrankings.service.steam.SteamDataExtractor;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.joda.time.DateTime;

/**
 *
 * @author Michael
 */
public class UpdateHandler extends AbstractHandler {

    private final static Logger LOGGER = Logger.getLogger(LeaderboardHandler.class.getName());

    private ProfileHandler profile;
    
    public UpdateHandler(ProfileHandler profile) {
        this.profile = profile;
    }
    
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Open DB connection
        Database.openDBConnection();

        Map<String, String[]> param = request.getParameterMap();

        // Check to see if parameters are correct
        if (param == null || param.isEmpty() || !param.containsKey(PARAMETERS_USER_ID)) {
            sendError(ErrorCodes.API_ERROR_BAD_ARGUMENTS, response, baseRequest);
            Database.closeDBConnection();
            return;
        }

        long id = SteamDataExtractor.convertToSteamId64(param.get("id")[0]);

        // Get user from DB, delete them
        Profile user = Profile.findById(id - SteamProfile.BASE_ID_64);
        user.deleteCascadeShallow();

        // Now we add them as a new user
        SteamDataExtractor steamDataExtractor = new SteamDataExtractor(new SteamApi(Initialization.CONFIG.getProperty("apikey")));

        SteamProfile steamProfile = steamDataExtractor.getSteamProfile(id);

        user = new Profile();
        user.set("id", (int) (steamProfile.getSteamId64() - SteamProfile.BASE_ID_64));
        user.set("community_id", steamProfile.getSteamCommunityId());
        user.set("persona_name", steamProfile.getPersonaName());
        user.set("real_name", steamProfile.getRealName());
        user.set("location_country", steamProfile.getCountryCode());
        user.set("location_province", steamProfile.getProvinceCode());
        user.set("location_city", steamProfile.getCityCode());
        user.set("avatar_full_url", steamProfile.getFullAvatarUrl());
        user.set("avatar_medium_url", steamProfile.getMediumAvatarUrl());
        user.set("avatar_icon_url", steamProfile.getIconAvatarUrl());

        user.set("last_logoff", new Timestamp(steamProfile.getLastOnline().getMillis()));
        user.set("avg_completion_rate", 0);

        user.insert();

        profile.processNewUser(steamDataExtractor, user, id);

        steamProfile = new SteamProfile(user.getInteger("id") + SteamProfile.BASE_ID_64, user.getString("community_id"), user.getString("persona_name"), user.getString("real_name"),
                user.getString("location_country"), user.getString("location_province"), user.getString("location_citys"), user.getString("avatar_full_url"),
                user.getString("avatar_medium_url"), user.getString("avatar_icon_url"), new DateTime(user.getTimestamp("last_logoff").getTime()));

        sendData(steamProfile.toString(), response, baseRequest);
        Database.closeDBConnection();

    }

}
