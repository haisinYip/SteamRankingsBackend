package com.steamrankings.service.core;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.javalite.activejdbc.LazyList;

import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.service.database.Database;
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.models.ProfilesAchievements;
import com.steamrankings.service.models.ProfilesGames;
import com.steamrankings.service.steam.SteamApi;
import com.steamrankings.service.steam.SteamDataExtractor;

/**
 * Runs update tasks every so often to keep the DB up to date.
 *
 */
public class Updater {

	private final ScheduledExecutorService scheduler =
		     Executors.newSingleThreadScheduledExecutor();

	private static final Logger logger = Logger
			.getLogger(Updater.class.getName());
	
	public Updater() {

		// Run every day
		try {
			Runnable update = new updateDatabase();
			scheduler.scheduleAtFixedRate(update, 0, 1, TimeUnit.DAYS );
		}
		catch (Exception e) {
			logger.warning("Error scheduling update task");
		}
		
	}

	private static class updateDatabase implements Runnable {
		

		@Override
		/**
		 * Updates the database with new info
		 */
		public void run() {
			
			logger.info("Database update task now running...");
			
			// Open Database connection
			Database.openDBConnection();
			
			// Get user list from DB
			LazyList<Profile> users = Profile.findAll();
			
			// Get list of IDs
			long[] idList = new long[users.size()];
			for (int i = 0; i < users.size(); i++) {
				Profile user = users.get(i);
				idList[i] = user.getLong("id") + SteamProfile.BASE_ID_64;
			}
			
			// Ask steam for new info
			SteamApi api = new SteamApi(Application.CONFIG.getProperty("apikey"));
			SteamDataExtractor steam = new SteamDataExtractor(api);
			ArrayList<SteamProfile> profileList = steam.getSteamProfileThreaded(idList);
			
			// Remove private profiles
			for (SteamProfile profile : profileList) {
				if (profile.getProfileState() != 3) {
					removeProfile(profile);
					logger.info("Private profile with ID " + profile.getSteamId64() + " and community name " + profile.getSteamCommunityId() + " is now private and has been removed.");
				}
			}
			
			// Close DB
			Database.closeDBConnection();
			
			logger.info("Database update task complete.");
		}
		
		/**
		 * Remove user from database, including all games/achievements
		 * 
		 * @param profile The SteamProfile of the user
		 */
		private void removeProfile(SteamProfile profile) {
			// Get profile from DB
			Profile user = Profile.findById(profile.getSteamId64() - SteamProfile.BASE_ID_64);
			if (user != null) {
					// Run shallow delete because our relationships are only 1 level
					// and this is fast
					user.deleteCascadeShallow();
				}
		}
		
		
	}
}
