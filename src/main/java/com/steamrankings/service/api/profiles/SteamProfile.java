package com.steamrankings.service.api.profiles;

public abstract class SteamProfile {
	public abstract String getSteamId64();
	public abstract String getSteamCommunityId();
	public abstract String getPersonaName();
	public abstract String getRealName();
	public abstract String getCountry();
	public abstract String getTotalPlayTime();
	public abstract String getSteamCommunityUrl();
}
