package com.steamrankings.service.api.achievements;

import org.joda.time.DateTime;

public abstract class SteamAchievement {
	private int appId;
	private String unlockedIconUrl;
	private String LockedIconUrl;
	private String name;
	private String description;
	private DateTime timestamp;
	
    public int getAppId(){
    	return this.appId;
    }
    public void setAppId(int appId){
    	this.appId=appId;
    }

    public  String getUnlockedIconUrl(){
    	return this.unlockedIconUrl;
    }
    
    public  void setUnlockedIconUrl(String unlockedIconUrl){
    	this.unlockedIconUrl=unlockedIconUrl;
    }

    public  String getLockedIconUrl(){
    	return this.LockedIconUrl;
    }
    public  void setLockedIconUrl(String lockedIconUrl){
    	 this.LockedIconUrl= lockedIconUrl;
    }

    public String getName() {
    	return this.name;
    }
    
    public void setName(String name) {
    	this.name= name;
    }

    public String getDescription(){
   	 return this.description;
   	
   }
    
    public void setDescription(String description){
    	 this.description= description;
    	
    }

    public  DateTime getTimestamp(){
    	return this.timestamp;
    }
    public  void setTimestamp(DateTime timestamp){
    	 this.timestamp= timestamp;
    }
}
