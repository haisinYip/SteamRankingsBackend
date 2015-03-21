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

import com.steamrankings.service.api.ErrorCodes;
import com.steamrankings.service.api.games.SteamGame;
import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.service.api.profiles.VersusResult;
import com.steamrankings.service.database.Database;
import com.steamrankings.service.models.Game;
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.models.ProfilesGames;
import com.steamrankings.service.steam.SteamDataExtractor;

public class VersusHandler extends AbstractHandler {
    public static final String PARAMETERS_USER_ID = "id";

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Open DB connection
        Database.openDBConnection();

        Map<String, String[]> param = request.getParameterMap();

        // Check to see if parameters are correct
        if (param == null || param.isEmpty() || !param.containsKey("id1") || !param.containsKey("id2")) {
            sendError(ErrorCodes.API_ERROR_BAD_ARGUMENTS, response, baseRequest);
            Database.closeDBConnection();
            return;
        }

        long steamId1 = SteamDataExtractor.convertToSteamId64(param.get("id1")[0]);
        long steamId2 = SteamDataExtractor.convertToSteamId64(param.get("id2")[0]);
        if (steamId1 == -1 || steamId2 == -1) {
            sendError(ErrorCodes.API_ERROR_STEAM_ID_INVALID, response, baseRequest);
            Database.closeDBConnection();
            return;
        }

        if (BlacklistHandler.isUserInBlackList(steamId1) || BlacklistHandler.isUserInBlackList(steamId2)) {
            sendError(ErrorCodes.API_ERROR_STEAM_ID_BLACKLIST, response, baseRequest);
            Database.closeDBConnection();
            return;
        }

        Profile profile1 = Profile.findById(steamId1 - SteamProfile.BASE_ID_64);
        Profile profile2 = Profile.findById(steamId2 - SteamProfile.BASE_ID_64);

        if (profile1 == null || profile2 == null) {
            sendError(ErrorCodes.API_ERROR_STEAM_ID_INVALID, response, baseRequest);
            Database.closeDBConnection();
            return;
        }

        List<ProfilesGames> user1Games = ProfilesGames.where("profile_id = ?", profile1.getId()).orderBy("game_id");
        List<ProfilesGames> user2Games = ProfilesGames.where("profile_id = ?", profile2.getId()).orderBy("game_id");

        ArrayList<Integer> user1GameIds = new ArrayList<Integer>();
        for (ProfilesGames user1Game : user1Games) {
            user1GameIds.add(user1Game.getInteger("game_id"));
        }

        ArrayList<Integer> user2GameIds = new ArrayList<Integer>();
        for (ProfilesGames user2Game : user2Games) {
            user2GameIds.add(user2Game.getInteger("game_id"));
        }

        user1GameIds.retainAll(user2GameIds);
        user2GameIds.retainAll(user1GameIds);

        int i = 0;
        int j = 0;

        ArrayList<VersusResult> comparisons = new ArrayList<VersusResult>();

        for (Integer gameId : user2GameIds) {
            int user1 = user1Games.get(i).getInteger("game_id");
            int user2 = user2Games.get(j).getInteger("game_id");

            while (user1 != gameId) {
                i++;
                user1 = user1Games.get(i).getInteger("game_id");
            }

            while (user2 != gameId) {
                j++;
                user2 = user2Games.get(j).getInteger("game_id");
            }

            Game game = Game.findById(gameId.intValue());

            VersusResult result = new VersusResult(new SteamGame(game.getInteger("id").intValue(), game.getString("icon_url"), game.getString("logo_url"), game.getString("name")), user1Games.get(i)
                    .getFloat("completion_rate"), user2Games.get(j).getFloat("completion_rate"));

            comparisons.add(result);
        }

        ObjectMapper mapper = new ObjectMapper();
        sendData(mapper.writeValueAsString(comparisons), response, baseRequest);

        Database.closeDBConnection();
    }
}
