package com.steamrankings.service.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.javalite.activejdbc.LazyList;

import com.steamrankings.service.database.Database;
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.models.ProfilesAchievements;
import com.steamrankings.service.models.ProfilesGames;

public class Updater {

	private final ScheduledExecutorService scheduler =
		     Executors.newSingleThreadScheduledExecutor();

	public Updater() {
		
		removeIDs(new int[] { 5460893 });
	}

	
	private void updateDatabase() {
		LazyList<Profile> users = Profile.findAll();
		for (Profile user: users) {
			
		}
	}
	
	/**
	 * Remove all specified IDs (SteamID64 - Base) from database, all tables
	 * 
	 * @param ids
	 */
	private void removeIDs(int[] ids) {
		for (int id : ids) {
			// Get user
			Profile user = Profile.findById(id);
			if (user != null) {
				// Run shallow delete because our relationships are only 1 level
				// and this is fast
				user.deleteCascadeShallow();
			}
		}

	}
}
