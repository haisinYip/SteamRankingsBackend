package com.steamrankings.service.core;

import com.steamrankings.service.api.ErrorCodes;
import com.steamrankings.service.api.leaderboards.RankEntryByAchievements;
import static com.steamrankings.service.core.ProfileHandler.PARAMETERS_USER_ID;
import static com.steamrankings.service.core.ResponseHandler.sendData;
import static com.steamrankings.service.core.ResponseHandler.sendError;
import com.steamrankings.service.database.Database;
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.models.ProfilesGames;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.javalite.activejdbc.LazyList;

public class TopPlayerHandler extends AbstractHandler {

    private final static Logger LOGGER = Logger.getLogger(LeaderboardHandler.class.getName());

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // Open DB connection
        Database.openDBConnection();

        Map<String, String[]> param = request.getParameterMap();

        // Check to see if parameters are correct
        if (param == null || param.isEmpty()) {
            sendError(ErrorCodes.API_ERROR_BAD_ARGUMENTS, response, baseRequest);
            Database.closeDBConnection();
            return;
        }

        // *****************************************************
        // Last minute hack by Michael
        // DON'T COPY THIS AS PRODUCTION CODE!
        // It does work as a good example of the power of our DB library though
        
        if (param.containsKey("country")) {
//    		List<RankEntryByAchievements> rankEntries = processGetCountryLeaderboard("0", "0", param.get("country")[0]);
//    		if(rankEntries == null || rankEntries.isEmpty()) {
//    			sendResponseUTF(socket, "HTTP/1.1 200" + CRLF, "Content-type: " + "application/json ; charset=UTF-8" + CRLF, "No players");
//    		} else {
//    			sendResponseUTF(socket, "HTTP/1.1 200" + CRLF, "Content-type: " + "application/json ; charset=UTF-8" + CRLF, rankEntries.get(0).getName());
//    		}
            /*
            This should be done with some fancier DB requests, using either advanced
            ActiveJDBC functions or raw SQL
            Also needs to be cached in DB
            */
            sendError("Not implemented", response, baseRequest);

        } else if (param.containsKey("game")) {
//    		List<RankEntryByAchievements> rankEntries = processGetGamesLeaderboard("0", "0", parameters.get("game"));
//    		if(rankEntries == null || rankEntries.isEmpty()) {
//    			sendResponseUTF(socket, "HTTP/1.1 200" + CRLF, "Content-type: " + "application/json ; charset=UTF-8" + CRLF, "No players");
//    		} else {
//    			sendResponseUTF(socket, "HTTP/1.1 200" + CRLF, "Content-type: " + "application/json ; charset=UTF-8" + CRLF, rankEntries.get(0).getName());
//    		}

            /*
            To be efficient, we should have one request that returns all top players, not one by one b/c 4000 requests = not good
            */
            LazyList<ProfilesGames> listOfGames = ProfilesGames.where("game_id = ?", param.get("game")[0]).orderBy("completion_rate desc");
            if (listOfGames.isEmpty()) {
                sendData("No players.", response, baseRequest);
            } else {
                sendData(listOfGames.get(0).parent(Profile.class).getString("persona_name"), response, baseRequest);
            }

        } else {
            sendError(ErrorCodes.API_ERROR_BAD_ARGUMENTS, response, baseRequest);
        }
        
        Database.closeDBConnection();

    }

}
