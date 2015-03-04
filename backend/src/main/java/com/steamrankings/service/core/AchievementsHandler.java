/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steamrankings.service.core;

import com.steamrankings.service.api.ErrorCodes;
import com.steamrankings.service.api.achievements.GameAchievement;
import com.steamrankings.service.api.profiles.SteamProfile;
import static com.steamrankings.service.core.GamesHandler.PARAMETERS_APP_ID;
import static com.steamrankings.service.core.ProfileHandler.PARAMETERS_USER_ID;
import static com.steamrankings.service.core.ResponseHandler.sendData;
import static com.steamrankings.service.core.ResponseHandler.sendError;
import com.steamrankings.service.database.Database;
import com.steamrankings.service.models.Achievement;
import com.steamrankings.service.models.ProfilesAchievements;
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
import org.joda.time.DateTime;

/**
 *
 * @author Michael
 */
public class AchievementsHandler extends AbstractHandler{
    
    private final static Logger LOGGER = Logger.getLogger(AchievementsHandler.class.getName());
    
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

        long steamId = -1;
        if (param.containsKey("id")) {
            steamId = SteamDataExtractor.convertToSteamId64(param.get("id")[0]);
        }

        if (param.containsKey(PARAMETERS_USER_ID) && param.containsKey(PARAMETERS_APP_ID)) {
            List<ProfilesAchievements> list = ProfilesAchievements.where("profile_id = ? AND game_id = ?", (int) (steamId - SteamProfile.BASE_ID_64),
                    Integer.parseInt(param.get(PARAMETERS_APP_ID)[0])).limit(15);
            
            ArrayList<ProfilesAchievements> profilesAchievements = new ArrayList<>(list);
            
            if (profilesAchievements != null) {
                
                ArrayList<GameAchievement> gameAchievements = new ArrayList<>();
                
                for (ProfilesAchievements profilesAchievement : profilesAchievements) {
                    Achievement achievement = Achievement.findFirst("id = ? AND game_id = ?", profilesAchievement.getInteger("achievement_id"), profilesAchievement.getInteger("game_id"));
                    if (achievement != null) {
                        gameAchievements.add(new GameAchievement(achievement.getInteger("game_id"), achievement.getString("id"), achievement.getString("name"), achievement.getString("description"),
                                achievement.getString("unlocked_icon_url"), achievement.getString("locked_icon_url"), new DateTime(profilesAchievement.getTimestamp("unlocked_timestamp").getTime())));
                    }
                }
                ObjectMapper mapper = new ObjectMapper();
                sendData(mapper.writeValueAsString(gameAchievements), response, baseRequest);
            }
        } 
        
        else if (param.containsKey(PARAMETERS_USER_ID)) {
            
            List<ProfilesAchievements> list = ProfilesAchievements.where("profile_id = ?", (int) (steamId - SteamProfile.BASE_ID_64)).limit(30);
            ArrayList<ProfilesAchievements> profilesAchievements = new ArrayList<>(list);
            
            if (profilesAchievements != null) {
                ArrayList<GameAchievement> gameAchievements = new ArrayList<>();
                
                for (ProfilesAchievements profilesAchievement : profilesAchievements) {
                    
                    Achievement achievement = Achievement.findFirst("id = ? AND game_id = ?", profilesAchievement.getInteger("achievement_id"), profilesAchievement.getInteger("game_id"));
                    if (achievement != null) {
                        gameAchievements.add(new GameAchievement(achievement.getInteger("game_id"), achievement.getString("id"), achievement.getString("name"), achievement.getString("description"),
                                achievement.getString("unlocked_icon_url"), achievement.getString("locked_icon_url"), new DateTime(profilesAchievement.getTimestamp("unlocked_timestamp").getTime())));
                    }
                }
                ObjectMapper mapper = new ObjectMapper();
                sendData(mapper.writeValueAsString(gameAchievements), response, baseRequest);
            }
        } 
        
        else if (param.containsKey(PARAMETERS_APP_ID)) {
            
            List<Achievement> list = ProfilesAchievements.where("game_id = ?", Integer.parseInt(param.get(PARAMETERS_APP_ID)[0]));
            
            ArrayList<Achievement> achievements = new ArrayList<>(list);
            ArrayList<GameAchievement> gameAchievements = new ArrayList<>();
            
            for (Achievement achievement : achievements) {
                gameAchievements.add(new GameAchievement(achievement.getInteger("game_id"), achievement.getString("id"), achievement.getString("name"), achievement.getString("description"),
                        achievement.getString("unlocked_icon_url"), achievement.getString("locked_icon_url")));
            }
            
            ObjectMapper mapper = new ObjectMapper();
            sendData(mapper.writeValueAsString(gameAchievements), response, baseRequest);

        } else {
            sendError(ErrorCodes.API_ERROR_BAD_ARGUMENTS, response, baseRequest);
        }
        
        Database.closeDBConnection();
    }
}
