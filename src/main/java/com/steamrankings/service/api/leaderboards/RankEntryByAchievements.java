package com.steamrankings.service.api.leaderboards;

public abstract class RankEntryByAchievements extends RankEntry {
	public abstract String getTotalNumberOfAchievements();
	public abstract String getAverageCompetionRate();
	public abstract String getCountry();
}
