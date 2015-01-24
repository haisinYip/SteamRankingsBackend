package com.steamrankings.service.api.games;

public abstract class SteamGame {
	public abstract int getAppId();
	public abstract String getIconUrl();
	public abstract String getName();
}