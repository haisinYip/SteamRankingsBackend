package com.steamrankings.service.api.games;

public class Game {
	
	private int appid;
	private String iconUrl;
	private String logoUrl;
	private String name;
	
	public Game(int appid, String iconUrl, String logoUrl, String name) {
		this.appid = appid;
		this.iconUrl = iconUrl;
		this.name = name;
	
	}
	
    public int getAppId() {
    	return appid;
    }

    public String getIconUrl() {
    	return iconUrl;
    }

    public String getLogoUrl() {
    	return logoUrl;
    }

    public String getName() {
    	return name;
    }

    public void setAppId(int appid) {
    	this.appid = appid;
    }

    public void setIconUrl(String iconUrl) {
    	this.iconUrl = iconUrl;
    }

    public void setLogoUrl(String logoUrl) {
    	this.logoUrl = logoUrl;
    }

    public void setName(String name) {
    	this.name = name;
    }
}