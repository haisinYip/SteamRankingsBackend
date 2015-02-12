package com.steamrankings.service.api.leaderboards;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class RankEntryByAchievements extends RankEntry {
    @JsonProperty("achievements_total")
    private int achievementsTotal;
    
    @JsonProperty("completion_rate")
    private String completionRate;
    
    @JsonProperty("country_code")
    private String countryCode;

    public RankEntryByAchievements() {
    }

    public RankEntryByAchievements(int achievementsTotal, String completitionRate, String countryCode) {
        this.achievementsTotal = achievementsTotal;
        this.completionRate = completitionRate;
        this.countryCode = countryCode;
    }

    public RankEntryByAchievements(int rankNumber, long id64, String name, int achievementsTotal, String completitionRate, String countryCode) {
        super(rankNumber, id64, name);
        this.achievementsTotal = achievementsTotal;
        this.completionRate = completitionRate;
        this.countryCode = countryCode;
    }

    @JsonIgnore
    public int getAchievementsTotal() {
        return this.achievementsTotal;
    }

    @JsonIgnore
    public String getCompletionRate() {
        return this.completionRate;
    }

    @JsonIgnore
    public String getCountryCode() {
        return this.countryCode;
    }
    
    @Override
    @JsonIgnore
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer();

        try {
            return writer.writeValueAsString(this);
        } catch (Exception e) {
            return null;
        }
    }

    @JsonIgnore
    public String toPrettyString() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

        try {
            return writer.writeValueAsString(this);
        } catch (Exception e) {
            return null;
        }
    }
}