/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steamrankings.service.core;

import com.steamrankings.service.api.ErrorCodes;
import com.steamrankings.service.api.achievements.GameAchievement;
import com.steamrankings.service.api.games.SteamGame;
import com.steamrankings.service.api.profiles.SteamProfile;
import static com.steamrankings.service.core.ResponseHandler.sendData;
import static com.steamrankings.service.core.ResponseHandler.sendError;
import com.steamrankings.service.database.Database;
import com.steamrankings.service.models.Game;
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.steam.SteamApi;
import com.steamrankings.service.steam.SteamDataExtractor;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.joda.time.DateTime;

/**
 *
 * @author Michael
 */
public class ProfileHandler extends AbstractHandler {

    private final static Logger LOGGER = Logger.getLogger(ProfileHandler.class.getName());

    public static final String PARAMETERS_USER_ID = "id";

    private static final int AVG_NUM_GAMES_NOT_IN_DB = 50;

    private final Updater updater;

    public ProfileHandler(Updater updater) {
        this.updater = updater;
    }

    public void processNewUser(SteamDataExtractor steamDataExtractor, Profile profile, long steamID64, Boolean getFriends) {

        long time = System.currentTimeMillis();
        // Get user game list
        HashMap<SteamGame, Integer> ownedGames = (HashMap<SteamGame, Integer>) steamDataExtractor.getPlayerOwnedGames(steamID64);

        // If user has no games -> no games,achievements and links between them to add so we exit here
        if (ownedGames == null) {
            return;
        }

        // Get list of all games + achievements in DB, convert to array of IDs
        LazyList<Game> gamesDB = Game.findAll();

        ArrayList<Long> idListDB = new ArrayList<>(gamesDB.size());
        gamesDB.stream().forEach((game) -> {
            idListDB.add(game.getLongId());
        });

        // Go through all games owned by player, check for missing ones with DB
        ArrayList<SteamGame> notContain = new ArrayList<>(AVG_NUM_GAMES_NOT_IN_DB);
        ownedGames.keySet().stream().filter((game) -> (!idListDB.contains((long) game.getAppId()))).forEach((game) -> {
            notContain.add(game);
        });

        // Define list of IDs from not games not in DB
        int[] idListNotContain = new int[notContain.size()];

        // Add all missing games to DB, create ID list for missing games at the
        // same time
        PreparedStatement ps = Base.startBatch("insert into games (id, name, icon_url, logo_url) values(?, ?, ?, ?)");

        int i = 0;
        for (SteamGame game : notContain) {
            Base.addBatch(ps, game.getAppId(), game.getName(), game.getIconUrl(), game.getLogoUrl());
            idListNotContain[i++] = game.getAppId();
        }

        Base.executeBatch(ps);

        // Get all achievements for all missing games
        ArrayList<GameAchievement> gameAchievements = (ArrayList<GameAchievement>) steamDataExtractor.getGameAchievementsThreaded(idListNotContain);

        // Add all missing achievements to DB
        ps = Base.startBatch("insert into achievements (id, apiname, game_id, name, description, unlocked_icon_url, locked_icon_url) values (?,?,?,?,?,?,?)");
        for (GameAchievement achievement : gameAchievements) {
            // Note achievement hash is the ID ("apiname" in JSON, e.g.
            // TF_PLAY_GAME_EVERYCLASS) + the human readable name (e.g. Head of
            // the Class)
            // to ensure enough variation for hashing. Some apinames are only
            // 2-3 characters long which leads to collisions
            Base.addBatch(ps, (achievement.getAchievementId() + achievement.getName()).hashCode(), achievement.getAchievementId(), achievement.getAppId(), achievement.getName(), achievement.getDescription(),
                    achievement.getUnlockedIconUrl(), achievement.getLockedIconUrl());
        }

        Base.executeBatch(ps);

        // Add links from profile to achievements
        // First create array of IDs
        int[] ownedGamesIdList = new int[ownedGames.size()];
        i = 0;
        for (SteamGame game : ownedGames.keySet()) {
            ownedGamesIdList[i++] = game.getAppId();
        }

        // Create array for completion rate
        float[] completionRate = new float[ownedGames.size()];

        // Call Steam API to get all achievements for all games player owns
        ArrayList<GameAchievement> playerAchievements = (ArrayList<GameAchievement>) steamDataExtractor.getPlayerAchievementsThreaded(steamID64, ownedGamesIdList, completionRate);

        // Add profile -> achievement links to DB
        ps = Base.startBatch("insert into profiles_achievements (profile_id, achievement_id, game_id, unlocked_timestamp) values (?,?,?,?)");
        for (GameAchievement achievement : playerAchievements) {
            // TODO: Timestamp is a placeholder
            Base.addBatch(ps, profile.getId(), (achievement.getAchievementId() + achievement.getName()).hashCode(), achievement.getAppId(), new Timestamp(659836800).toString());
        }

        Base.executeBatch(ps);

        // Add all links from profile to each game, also calculate completion
        // ratio
        ps = Base.startBatch("insert into profiles_games (profile_id, game_id, total_play_time, completion_rate) values (?,?,?,?)");
        i = 0;
        for (Entry<SteamGame, Integer> ownedGame : ownedGames.entrySet()) {
            Base.addBatch(ps, profile.getId(), ownedGame.getKey().getAppId(), ownedGame.getValue(), completionRate[i++]);
        }
        Base.executeBatch(ps);

        float avgCompletionRate = 0;
        if (completionRate.length != 0) {
            avgCompletionRate = mean(completionRate);
        }
        profile.setFloat("avg_completion_rate", avgCompletionRate);
        profile.saveIt();
        
        if (getFriends) {
            ps = Base.startBatch("insert into profiles_profiles (profile_id1, profile_id2) values (?,?)");
            long[] friendIds = steamDataExtractor.getSteamFriends(steamID64);

            if (friendIds != null && friendIds.length != 0) {
                ArrayList<Long> idsNotInDatabase = new ArrayList<Long>();
                
                for(int j = 0; j < friendIds.length; j++) {
                    if(!Profile.exists(friendIds[j] - SteamProfile.BASE_ID_64)) {
                        idsNotInDatabase.add(friendIds[j]);
                    } else {
                        Base.addBatch(ps, profile.getId(), friendIds[j] - SteamProfile.BASE_ID_64);
                    }
                }

                if (idsNotInDatabase.size() > 0) {
                    long[] idsToFind = new long[idsNotInDatabase.size()];
                    
                    for(int j = 0; j < idsNotInDatabase.size(); j++) {
                        idsToFind[j] = idsNotInDatabase.get(j).longValue();
                    }

                    ArrayList<SteamProfile> friendProfiles = steamDataExtractor.getSteamProfileThreaded(idsToFind);
                    if (friendProfiles != null && friendProfiles.size() != 0) {
                        for (SteamProfile friendProfile : friendProfiles) {
                            if (friendProfile.getProfileState() == 3) {
                                Profile friendModel = new Profile();

                                friendModel.set("id", (int) (friendProfile.getSteamId64() - SteamProfile.BASE_ID_64));
                                friendModel.set("community_id", friendProfile.getSteamCommunityId());
                                friendModel.set("persona_name", friendProfile.getPersonaName());
                                friendModel.set("real_name", friendProfile.getRealName());
                                friendModel.set("location_country", friendProfile.getCountryCode());
                                friendModel.set("location_province", friendProfile.getProvinceCode());
                                friendModel.set("location_city", friendProfile.getCityCode());
                                friendModel.set("avatar_full_url", friendProfile.getFullAvatarUrl());
                                friendModel.set("avatar_medium_url", friendProfile.getMediumAvatarUrl());
                                friendModel.set("avatar_icon_url", friendProfile.getIconAvatarUrl());

                                friendModel.set("last_logoff", new Timestamp(friendProfile.getLastOnline().getMillis()));
                                friendModel.set("avg_completion_rate", 0);
                                friendModel.insert();
                                processNewUser(steamDataExtractor, friendModel, friendProfile.getSteamId64(), false);

                                Base.addBatch(ps, profile.getId(), friendModel.getId());
                            }
                        }
                    }
                }
            }
            
            Base.executeBatch(ps);
        }

        // Close prepared statement because we're done with it
        try {
            ps.close();
        } catch (SQLException e) {
            LOGGER.warning("Unable to close prepared statement in batch processing");
        }

        System.out.println("Time taken to add new user: " + (System.currentTimeMillis() - time));

        // Tell updater we have added the achievements; start getting the percentage info
        updater.startAchievementPercentageUpdate(gameAchievements);
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

        long steamId = SteamDataExtractor.convertToSteamId64(param.get(PARAMETERS_USER_ID)[0]);
        if (steamId == -1) {
            sendError(ErrorCodes.API_ERROR_STEAM_ID_INVALID, response, baseRequest);
            Database.closeDBConnection();
            return;
        }

        SteamApi steamApi = new SteamApi(Initialization.CONFIG.getProperty("apikey"));
        SteamDataExtractor steamDataExtractor = new SteamDataExtractor(steamApi);

        if (BlacklistHandler.isUserInBlackList(steamId)) {
            sendError(ErrorCodes.API_ERROR_STEAM_ID_BLACKLIST, response, baseRequest);
            Database.closeDBConnection();
            return;
        }

        Profile profile = Profile.findById((int) (steamId - SteamProfile.BASE_ID_64));
        SteamProfile steamProfile = null;

        if (profile == null) {
            steamProfile = steamDataExtractor.getSteamProfile(steamId);
            if (steamProfile == null) {
                sendError(ErrorCodes.API_ERROR_STEAM_USER_DOES_NOT_EXIST, response, baseRequest);
                Database.closeDBConnection();
                return;
            } else {

                profile = new Profile();
                profile.set("id", (int) (steamProfile.getSteamId64() - SteamProfile.BASE_ID_64));
                profile.set("community_id", steamProfile.getSteamCommunityId());
                profile.set("persona_name", steamProfile.getPersonaName());
                profile.set("real_name", steamProfile.getRealName());
                profile.set("location_country", steamProfile.getCountryCode());
                profile.set("location_province", steamProfile.getProvinceCode());
                profile.set("location_city", steamProfile.getCityCode());
                profile.set("avatar_full_url", steamProfile.getFullAvatarUrl());
                profile.set("avatar_medium_url", steamProfile.getMediumAvatarUrl());
                profile.set("avatar_icon_url", steamProfile.getIconAvatarUrl());

                profile.set("last_logoff", new Timestamp(steamProfile.getLastOnline().getMillis()));
                profile.set("avg_completion_rate", 0);
                profile.insert();

                LOGGER.log(Level.INFO, "New Profile Added: {0}", profile.toString());
                processNewUser(steamDataExtractor, profile, steamProfile.getSteamId64(), true);
            }
        }

        steamProfile = new SteamProfile(profile.getInteger("id") + SteamProfile.BASE_ID_64, profile.getString("community_id"), profile.getString("persona_name"), profile.getString("real_name"),
                profile.getString("location_country"), profile.getString("location_province"), profile.getString("location_citys"), profile.getString("avatar_full_url"),
                profile.getString("avatar_medium_url"), profile.getString("avatar_icon_url"), new DateTime(profile.getTimestamp("last_logoff").getTime()));

        sendData(steamProfile.toString(), response, baseRequest);

        Database.closeDBConnection();
    }

    /**
     * Calculate the mean of a float array.
     *
     * @param p Float array
     * @return Mean
     */
    private static float mean(float[] p) {

        float sum = 0;

        for (int i = 0; i < p.length; i++) {
            sum += p[i];
        }
        return sum / p.length;
    }
}
