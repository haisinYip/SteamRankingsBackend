package com.steamrankings.service.api.achievements;

import java.util.List;

import com.steamrankings.service.api.games.SteamGame;

public class AchievementWrapper {
	
	private List<SteamAchievement> achievements;                             
    
    public List<SteamAchievement> getAchievements() {                        
        return this.achievements;                                               
    }      

}
