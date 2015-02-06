package com.steamrankings.service.api.leaderboards;

public abstract class RankEntry {

    private String rankNumber;
    private String name;
    private String id64;

    public String getRankNumber() {
        return this.rankNumber;
    }

    public void setRankNumber(String rankNumber) {
        this.rankNumber = rankNumber;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId64() {
        return this.id64;
    }

    public void setId64(String id64) {
        this.id64 = id64;
    }
}
