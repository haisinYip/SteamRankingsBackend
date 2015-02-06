package com.steamrankings.service.api.leaderboards;

public abstract class RankEntryByAchievements extends RankEntry {

    private String totalNumberOfAchievments;
    private String AverageCompetionRate;
    private String country;

    public String getTotalNumberOfAchievements() {
        return this.totalNumberOfAchievments;
    }

    public void setTotalNumberOfAchievements(String totalNumberOfAchievments) {
        this.totalNumberOfAchievments = totalNumberOfAchievments;
    }

    public String getAverageCompetionRate() {
        return this.AverageCompetionRate;
    }

    public void setAverageCompetionRate(String AverageCompetionRate) {
        this.AverageCompetionRate = AverageCompetionRate;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
