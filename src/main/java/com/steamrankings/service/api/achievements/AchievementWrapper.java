package com.steamrankings.service.api.achievements;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class AchievementWrapper {

    @JsonProperty("achievements")
    private List<GameAchievement> achievements;

    @JsonIgnore
    public List<GameAchievement> getAchievements() {
        return this.achievements;
    }

}
