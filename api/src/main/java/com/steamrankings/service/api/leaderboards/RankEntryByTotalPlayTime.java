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

	public RankEntryByTotalPlayTime() {	
	}

	public RankEntryByTotalPlayTime(int totalPlayTime, String countryCode) {
		this.totalPlayTime = totalPlayTime;
		this.countryCode = countryCode;
	}

	public RankEntryByTotalPlayTime(int rankNumber, long id64, String name, int totalPlayTime, String countryCode) {
		super(rankNumber, id64, name);
		this.totalPlayTime = totalPlayTime;
		this.countryCode = countryCode;
	}

	@JsonIgnore
	public int getTotalPlayTime() {
		return this.totalPlayTime;
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

