/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steamrankings.service.core;

import com.steamrankings.service.api.achievements.GameAchievement;
import com.steamrankings.service.api.games.SteamGame;
import com.steamrankings.service.api.profiles.SteamProfile;

import static com.steamrankings.service.core.ProfileHandler.PARAMETERS_USER_ID;
import static com.steamrankings.service.core.ResponseHandler.sendData;
import static com.steamrankings.service.core.ResponseHandler.sendError;

import com.steamrankings.service.database.Database;
import com.steamrankings.service.models.Achievement;
import com.steamrankings.service.models.Game;
import com.steamrankings.service.models.ProfilesGames;
import com.steamrankings.service.steam.SteamDataExtractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *
 * @author Michael
 */
public class GamesHandler extends AbstractHandler {

    private final static Logger LOGGER = Logger.getLogger(GamesHandler.class.getName());

    public static final String PARAMETERS_APP_ID = "appId";
    public static final String PARAMETERS_RAREST_APP_ID = "appIdRarest";

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // Open DB connection
        Database.openDBConnection();

        Map<String, String[]> param = request.getParameterMap();

        long steamId = -1;
        if (param.containsKey("id")) {
            steamId = SteamDataExtractor.convertToSteamId64(param.get("id")[0]);
        }

        if (param.containsKey(PARAMETERS_APP_ID)) {
            Game game = Game.findById(param.get("appId")[0]);

            if (game == null) {
                sendError("A game with an ID " + param.get("appId")[0] + " does not exist", response, baseRequest);

            } else {
                ObjectMapper mapper = new ObjectMapper();
                SteamGame steamGame = new SteamGame(game.getInteger("id"), game.getString("icon_url"), game.getString("logo_url"), game.getString("name"));
                sendData(mapper.writeValueAsString(steamGame), response, baseRequest);
            }
        } else if (param.containsKey(PARAMETERS_USER_ID)) {

            List<ProfilesGames> list = ProfilesGames.where("profile_id = ?", (int) (steamId - SteamProfile.BASE_ID_64)).orderBy("total_play_time desc").limit(30);

            ArrayList<ProfilesGames> profilesGames = new ArrayList<>(list);
            if (profilesGames != null) {

                ArrayList<SteamGame> steamGames = new ArrayList<>();

                for (ProfilesGames profilesGame : profilesGames) {
                    Game game = Game.findById(profilesGame.get("game_id"));
                    if (game != null) {
                        steamGames.add(new SteamGame(game.getInteger("id"), game.getString("icon_url"), game.getString("logo_url"), game.getString("name")));
                    }
                }
                ObjectMapper mapper = new ObjectMapper();
                sendData(mapper.writeValueAsString(steamGames), response, baseRequest);
            }
        } else if(param.containsKey(PARAMETERS_RAREST_APP_ID)){
        	
        	 System.out.println("Rarest achievments");
        	 List<Achievement> list = Achievement.where("game_id = ?", param.get("appIdRarest")[0]).orderBy("percentageComplete").limit(30);

             ArrayList<Achievement> achievements = new ArrayList<>(list);
             if (achievements != null) {

                 ArrayList<GameAchievement> rarestAchievements = new ArrayList<>();

                 for (Achievement achievement : achievements) {
                	 System.out.println(achievement.getString("name"));
                	 rarestAchievements.add(new GameAchievement(achievement.getInteger("game_id"), null, achievement.getString("name"), achievement.getString("description"),achievement.getString("unlocked_icon_url"),achievement.getString("locked_icon_url"),achievement.getDouble("percentageComplete")));
                 }
                 ObjectMapper mapper = new ObjectMapper();
                 sendData(mapper.writeValueAsString(rarestAchievements), response, baseRequest);
            }
        }
        else {

            List<Game> list = Game.findAll();
            ArrayList<Game> games = new ArrayList<>(list);

            if (games != null) {
                ArrayList<SteamGame> steamGames = new ArrayList<>();

                for (Game game : games) {
                    steamGames.add(new SteamGame(game.getInteger("id"), game.getString("icon_url"), game.getString("logo_url"), game.getString("name")));
                }

                ObjectMapper mapper = new ObjectMapper();
                sendData(mapper.writeValueAsString(steamGames), response, baseRequest);
            }
        }

        Database.closeDBConnection();
    }
}
