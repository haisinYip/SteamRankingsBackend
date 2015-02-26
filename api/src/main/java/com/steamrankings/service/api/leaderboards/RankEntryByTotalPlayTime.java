package com.steamrankings.service.api.leaderboards;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class RankEntryByTotalPlayTime extends RankEntry{
	@JsonProperty("total_play_time")
	private int totalPlayTime;

	@JsonProperty("country_code")
	private String countryCode;

    @JsonProperty("achievements_total")
    private int achievementsTotal;
    
    @JsonProperty("completion_rate")
    private String completionRate;
    
	public RankEntryByTotalPlayTime() {	
	}

	public RankEntryByTotalPlayTime(int totalPlayTime, int achievementsTotal, String completionRate, String countryCode) {
		this.totalPlayTime = totalPlayTime;
		this.countryCode = countryCode;
		this.achievementsTotal = achievementsTotal;
		this.completionRate = completionRate;
	}

	public RankEntryByTotalPlayTime(int rankNumber, long id64, String name, int totalPlayTime, int achievementsTotal, String completionRate, String countryCode) {
		super(rankNumber, id64, name);
		this.totalPlayTime = totalPlayTime;
		this.countryCode = countryCode;
		this.achievementsTotal = achievementsTotal;
		this.completionRate = completionRate;
	}

	@JsonIgnore
	public int getTotalPlayTime() {
		return this.totalPlayTime;
	}

	@JsonIgnore
	public String getCountryCode() {
		return this.countryCode;
	}

    @JsonIgnore
    public int getAchievementsTotal() {
        return this.achievementsTotal;
    }

    @JsonIgnore
    public String getCompletionRate() {
        return this.completionRate;
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

