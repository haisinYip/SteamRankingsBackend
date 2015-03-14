package com.steamrankings.service.core;

import com.steamrankings.service.api.achievements.GameAchievement;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.javalite.activejdbc.LazyList;

import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.service.database.Database;
import com.steamrankings.service.models.Achievement;
import com.steamrankings.service.models.Game;
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.steam.SteamApi;
import com.steamrankings.service.steam.SteamDataExtractor;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import org.javalite.activejdbc.Base;

/**
 * Runs update tasks every so often to keep the DB up to date.
 *
 */
public class Updater {

    //Threshold to use when deciding to update a DB achievement percentage or not
    private static final Double percentageThreshold = 0.001;

    private final ScheduledExecutorService scheduler
            = Executors.newSingleThreadScheduledExecutor();

    private static final Logger logger = Logger
            .getLogger(Updater.class.getName());

    /**
     * Constructor - schedules a recurring database update task
     */
    public Updater() {

        // Run every day
        try {
            Runnable update = new updateDatabaseScheduled();
            scheduler.scheduleAtFixedRate(update, 0, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            logger.warning("Error scheduling database update task");
        }

    }

    public void startAchievementPercentageUpdate(ArrayList<GameAchievement> missingAchievements) {

        try {

            Runnable update = new updateAchievRequest(missingAchievements);
            scheduler.submit(update);
        } catch (Exception e) {
            logger.warning("Error scheduling achievement update task");
        }
    }

    /**
     * Class for scheduled (e.g. daily) database updates
     */
    private static class updateDatabaseScheduled implements Runnable {

        @Override
        /**
         * Updates the database with new info
         */
        public void run() {
            logger.info("Database update task now running...");

            // Open Database connection
            Database.openDBConnection();
            removePrivateProfiles();
            updateAllAchievementPercentage();
            // Close DB
            Database.closeDBConnection();

            logger.info("Database update task complete.");
        }

    }
    
    /**
     * Class for on-demand achievement update requests
     */
    private static class updateAchievRequest implements Runnable {

        private final ArrayList<GameAchievement> missingAchievements;

        public updateAchievRequest(ArrayList<GameAchievement> missingAchievements) {
            this.missingAchievements = missingAchievements;
        }

        @Override
        public void run() {
            // Open Database connection
            Database.openDBConnection();

            updateSelectedAchievementPercentage(missingAchievements);

            // Close DB connection
            Database.closeDBConnection();
        }

    }
    
    /**
     * Updates all selected achievements with percentage completion rates.  Assumes these achievements
     * have been newly added to the database and do not have this information.
     * @param missingAchievements An ArrayList of achievements that were just added to the database
     */
    private static void updateSelectedAchievementPercentage(ArrayList<GameAchievement> missingAchievements) {

        // Get list of gameIds from achievement list
        ArrayList<Integer> gameIds = new ArrayList<>(100);
        for (GameAchievement achiev : missingAchievements) {
            if (!gameIds.contains(achiev.getAppId())) {
                gameIds.add(achiev.getAppId());
            }
        }
        
        SteamApi steamApi = new SteamApi(Initialization.CONFIG.getProperty("apikey"));
        SteamDataExtractor steamDataExtractor = new SteamDataExtractor(steamApi);

        // Get data for all games
        HashMap<Integer, HashMap<String, Double>> steamPercentages = steamDataExtractor.getGlobalAchievementPercent(gameIds);

        //Update each achievement
        PreparedStatement ps = Base.startBatch("update achievements set percentageComplete=? where id=? and game_id=?");
        for (GameAchievement achiev : missingAchievements) {
            
            Double percentage = steamPercentages.get(achiev.getAppId()).get(achiev.getAchievementId());

            // Percentage not returned by steam, we assume it's ~0% of people
            // Seems to happen when we start talking about 0.x % of players
            if (percentage == null) {
                percentage = 0.0;
            }

            Base.addBatch(ps, percentage, (achiev.getAchievementId() + achiev.getName()).hashCode(), achiev.getAppId());
        }

        try {
            Base.executeBatch(ps);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        try {
            ps.close();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private static void updateAllAchievementPercentage() {

        // Get list of achievements
        LazyList<Achievement> achievements = Achievement.findAll();

        // Find out IDs of all games with achievements in our DB
        // This will obviously skip games with no achievements
        ArrayList<Integer> gameIds = new ArrayList<>(Game.count().intValue());

        for (Achievement achievement : achievements) {
            if (!gameIds.contains(achievement.getInteger("game_id"))) {
                gameIds.add(achievement.getInteger("game_id"));
            }
        }

        SteamApi steamApi = new SteamApi(Initialization.CONFIG.getProperty("apikey"));
        SteamDataExtractor steamDataExtractor = new SteamDataExtractor(steamApi);

        // Get data for all of them
        HashMap<Integer, HashMap<String, Double>> steamPercentages = steamDataExtractor.getGlobalAchievementPercent(gameIds);

        //Update each record with percentage
        PreparedStatement ps = Base.startBatch("update achievements set percentageComplete=? where id=? and game_id=?");
        for (Achievement achiev : achievements) {
            Integer gameId = achiev.getInteger("game_id");
            Integer id = achiev.getInteger("id");
            Double percentage = steamPercentages.get(gameId).get(achiev.getString("apiname"));

            // Percentage not returned by steam, we assume it's ~0% of people
            // Seems to happen when we start talking about 0.x % of players
            if (percentage == null) {
                percentage = 0.0;
            }

            // Get the percentage we have in the DB (already in memory)
            Double percentageDB = achiev.getDouble("percentageComplete");

            Double difference = Math.abs(percentage - percentageDB);
            // Only add to the update list if there is an update to be made
            if (difference > percentageThreshold) {
                Base.addBatch(ps, percentage, id, gameId);
            }

        }

        try {
            Base.executeBatch(ps);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        try {
            ps.close();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private static void removePrivateProfiles() {

        // Get user list from DB
        LazyList<Profile> users = Profile.findAll();

        // Get list of IDs
        long[] idList = new long[users.size()];
        for (int i = 0; i < users.size(); i++) {
            Profile user = users.get(i);
            idList[i] = user.getLong("id") + SteamProfile.BASE_ID_64;
        }

        // Ask steam for new info
        SteamApi api = new SteamApi(Initialization.CONFIG.getProperty("apikey"));
        SteamDataExtractor steam = new SteamDataExtractor(api);
        ArrayList<SteamProfile> profileList = steam.getSteamProfileThreaded(idList);

        // Remove private profiles
        for (SteamProfile profile : profileList) {
            if (profile.getProfileState() != 3) {
                removeProfile(profile);
                logger.log(Level.INFO, "Private profile with ID {0} and community name {1} is now private and has been removed.", new Object[]{profile.getSteamId64(), profile.getSteamCommunityId()});
            }
        }

    }

    /**
     * Remove user from database, including all games/achievements
     *
     * @param profile The SteamProfile of the user
     */
    private static void removeProfile(SteamProfile profile) {
        // Get profile from DB
        Profile user = Profile.findById(profile.getSteamId64() - SteamProfile.BASE_ID_64);
        if (user != null) {
                // Run shallow delete because our relationships are only 1 level
            // and this is fast
            user.deleteCascadeShallow();
        }
    }

}
