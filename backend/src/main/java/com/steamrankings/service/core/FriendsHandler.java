package com.steamrankings.service.core;

import static com.steamrankings.service.core.ResponseHandler.sendData;
import static com.steamrankings.service.core.ResponseHandler.sendError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.joda.time.DateTime;

import com.steamrankings.service.api.ErrorCodes;
import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.service.database.Database;
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.models.ProfilesProfiles;
import com.steamrankings.service.steam.SteamDataExtractor;

public class FriendsHandler extends AbstractHandler {
    public static final String PARAMETERS_USER_ID = "id";

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Open DB connection
        Database.openDBConnection();

        try {
            Map<String, String[]> param = request.getParameterMap();

            // Check to see if parameters are correct
            if (param == null || param.isEmpty() || !param.containsKey(PARAMETERS_USER_ID)) {
                sendError(ErrorCodes.API_ERROR_BAD_ARGUMENTS, response, baseRequest);
                Database.closeDBConnection();
                return;
            }

            long steamId = SteamDataExtractor.convertToSteamId64(param.get(PARAMETERS_USER_ID)[0]);
            if (steamId == -1) {
                sendError(ErrorCodes.API_ERROR_STEAM_ID_INVALID, response, baseRequest);
                Database.closeDBConnection();
                return;
            }

            if (BlacklistHandler.isUserInBlackList(steamId)) {
                sendError(ErrorCodes.API_ERROR_STEAM_ID_BLACKLIST, response, baseRequest);
                Database.closeDBConnection();
                return;
            }

            List<ProfilesProfiles> friends = ProfilesProfiles.where("profile_id1 = ?", steamId - SteamProfile.BASE_ID_64);
            ArrayList<Profile> friendProfiles = new ArrayList<Profile>();

            for (ProfilesProfiles friend : friends) {
                friendProfiles.add(Profile.findById(friend.get("profile_id2")));
            }

            ArrayList<SteamProfile> friendSteamProfiles = new ArrayList<SteamProfile>();
            for (Profile friendProfile : friendProfiles) {
                friendSteamProfiles.add(new SteamProfile(friendProfile.getInteger("id") + SteamProfile.BASE_ID_64, friendProfile.getString("community_id"), friendProfile.getString("persona_name"),
                        friendProfile.getString("real_name"), friendProfile.getString("location_country"), friendProfile.getString("location_province"), friendProfile.getString("location_city"),
                        friendProfile.getString("avatar_full_url"), friendProfile.getString("avatar_medium_url"), friendProfile.getString("avatar_icon_url"), new DateTime(friendProfile.getTimestamp(
                                "last_logoff").getTime())));
            }

            ObjectMapper mapper = new ObjectMapper();
            sendData(mapper.writeValueAsString(friendSteamProfiles), response, baseRequest);
        } catch (Exception e) {
            Database.closeDBConnection();
        }

        Database.closeDBConnection();
    }

}
