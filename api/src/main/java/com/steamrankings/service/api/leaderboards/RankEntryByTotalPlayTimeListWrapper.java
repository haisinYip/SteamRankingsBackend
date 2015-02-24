package com.steamrankings.service.api.leaderboards;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class RankEntryByTotalPlayTimeListWrapper {
	
	 @JsonProperty("rank_entries")
	    private List<RankEntryByTotalPlayTime> rankEntries;

	    public RankEntryByTotalPlayTimeListWrapper() {
	    }

	    @JsonIgnore
	    public List<RankEntryByTotalPlayTime> getRankEntryByTotalPlayTime() {
	        return this.rankEntries;
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
