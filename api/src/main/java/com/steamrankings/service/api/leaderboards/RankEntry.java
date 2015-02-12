package com.steamrankings.service.api.leaderboards;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class RankEntry {
    @JsonProperty("rank")
    private int rankNumber;

    @JsonProperty("id64")
    private long id64;

    @JsonProperty("name")
    private String name;

    public RankEntry() {
    }

    public RankEntry(int rankNumber, long id64, String name) {
        this.rankNumber = rankNumber;
        this.id64 = id64;
        this.name = name;
    }

    @JsonIgnore
    public int getRankNumber() {
        return this.rankNumber;
    }
    @JsonIgnore
    public void setRankNumber(int rank) {
        rankNumber=rank;
    }

    @JsonIgnore
    public String getName() {
        return this.name;
    }

    @JsonIgnore
    public long getId64() {
        return this.id64;
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