package com.steamrankings.service.api.profiles;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.steamrankings.service.api.games.SteamGame;

public class VersusResult {
    @JsonProperty("steam_game")
    private SteamGame game;
    
    @JsonProperty("completion_rate_of_user_1")
    private float user1CompletionRate;
    
    @JsonProperty("completion_rate_of_user_2")
    private float user2CompletionRate;
    
    public VersusResult() {
        
    }
    
    public VersusResult(SteamGame game, float user1CompletionRate, float user2CompletionRate) {
        this.game = game;
        this.user1CompletionRate = user1CompletionRate;
        this.user2CompletionRate = user2CompletionRate;
    }
    
    @JsonIgnore
    public SteamGame getGame() {
        return game;
    }
    
    @JsonIgnore
    public float getCompletionRateOfUser1() {
        return user1CompletionRate;
    }
    
    @JsonIgnore
    public float getCompletionRateOfUser2() {
        return user2CompletionRate;
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
