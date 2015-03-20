package com.steamrankings.service.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.steamrankings.service.api.ErrorCodes;
import com.steamrankings.service.api.news.SteamNews;
import com.steamrankings.service.database.Database;
import com.steamrankings.service.models.Game;
import com.steamrankings.service.steam.SteamApi;
import com.steamrankings.service.steam.SteamDataExtractor;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.steamrankings.service.core.ResponseHandler.sendData;
import static com.steamrankings.service.core.ResponseHandler.sendError;

/**
 *
 * @author Sean
 *
 */
public class NewsHandler extends AbstractHandler {

    private final static int NUM_NEWS_ENTRIES = 1;
    private final static int MAX_LENGTH_NEWS_ENTRY = 1000;

    private final static Logger LOGGER = Logger.getLogger(NewsHandler.class.getName());

    /**
     * Testing this is tricky since a random game is pulled from the database
     * you cannot do
     * http://localhost:6789/news/?appId=440&count=1&maxlength=1000 for example,
     * will cause a null pointer error because the appId sent to getGameNews
     * will not match(unless you get really lucky). To test do: List<SteamNews>
     * sn = steamDataExtractor.getGameNews("appId", NUM_NEWS_ENTRIES,
     * MAX_LENGTH_NEWS_ENTRY); and supply your own appId thus bypassing the
     * random game generator. This will cause the appId to match and from the
     * testing I've done so far it seems to work fine. Make sure the appId in
     * the steamdataextractor and the one you supply in your web browser are the
     * same!
     *
     * @param target
     * @param baseRequest
     * @param request
     * @param response
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

        Map<String, String[]> param = request.getParameterMap();

        // Check to see if parameters are correct
        if (param == null || param.isEmpty()) {
            sendError(ErrorCodes.API_ERROR_BAD_ARGUMENTS, response, baseRequest);
            return;
        }

        //open db
        Database.openDBConnection();

        SteamDataExtractor steamDataExtractor = new SteamDataExtractor(new SteamApi(Initialization.CONFIG.getProperty("apikey")));

        SteamNews gameNews = null;
        
      //appID's of some popular games so that any random game selected has news and will not return null to the front end
      	int[] appIdArray = {440, 20, 10, 220, 360, 550, 570, 730, 8930, 22380, 39120, 42690, 40, 6850, 80, 100, 240, 320, 340, 4760};
      		
      	List<Game> gameslist = new ArrayList<Game>();
		for(int i = 0; i < appIdArray.length; i++) {
			Game addedGame = Game.findById(appIdArray[i]);
			if(addedGame == null) {
				continue;
			}
			else {
				gameslist.add(addedGame);
			}
		}
		
		if(gameslist.isEmpty()) {
			sendError(ErrorCodes.API_ERROR_BAD_ARGUMENTS, response, baseRequest);
            Database.closeDBConnection();
            return;
		}

        int gameslistsize = gameslist.size();

        //get a random number to select a random game from the gameslist
        Random randomGenerator = new Random();
        int randomGameEntry = randomGenerator.nextInt(gameslistsize);

        Game game = gameslist.get(randomGameEntry);
        //probably need a better error message
        if (game == null) {
            sendError(ErrorCodes.API_ERROR_BAD_ARGUMENTS, response, baseRequest);
            Database.closeDBConnection();
            return;
        } else {
            //get news for randomly selected game
            List<SteamNews> sn = null;
            sn = steamDataExtractor.getGameNews(game.getInteger("id"), NUM_NEWS_ENTRIES, MAX_LENGTH_NEWS_ENTRY);

            //may need adjusting. getGameNews returns a list of steam news, but if only one news entry is requested 
            //returning a list seems like bad practice
            //TODO: change getGameNews to return one SteamNews instead of a list or some other modification incase we want to expand on this and return more than one news entry
            //TODO: for some reason it still displays date in unix time..
            // I added the try catch at the request of Anthony to avoid errors that kill the thread 
            // and never close the database, breaking every other request
            // ex. leaderboard, games,...anything because the database is open already
            // -- Michael
            try {
                gameNews = new SteamNews(sn.get(0).getAppId(), sn.get(0).getTitle(), sn.get(0).getUrl(), sn.get(0).getContents(),
                        sn.get(0).getDate());
                sendData(gameNews.toString(), response, baseRequest);
                LOGGER.log(Level.INFO, "Game News Added");
            } catch (IndexOutOfBoundsException e) {
                LOGGER.log(Level.WARNING, "Error getting news: {0}", e.getMessage());
                sendError(ErrorCodes.API_ERROR_BAD_ARGUMENTS, response, baseRequest);
            }
        }

        Database.closeDBConnection();
    }

}
