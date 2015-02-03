package com.steamrankings.service.api.profiles;

import org.joda.time.DateTime;

public class SteamProfile {
	private String id64;
	private String communityID;
	private String personaName;
	private String realName;
	private String countryCode;
	private String lastOnline;
	private String totalPlayTime;
	private String avatar;
	
	public SteamProfile(String id64, String communityID, String personaName) {
		this.id64 = id64;
		this.communityID = communityID;
		this.personaName = personaName;
	}
	
	public String getSteamId64() {
		return id64;
	}
	public String getSteamCommunityId() {
		return communityID;
	}
	public String getPersonaName() {
		return personaName;
	}
	public String getRealName() {
		return realName;
	}
	public String getCountry() {
		return countryCode;
	}
	public String getTotalPlayTime() {
		return totalPlayTime;
	}
	public String getSteamCommunityUrl() {
		return null;
	}
	public String getLastOnlineTime() {
		return lastOnline;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setSteamId64(String steamID64) {
		this.id64 = steamID64;
	}
	public void setSteamCommunityId(String communityID) {
		this.communityID = communityID;
	}
	public void setPersonaName(String personaName) {
		this.personaName = personaName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public void setLastOnlineTime(String lastOnline) {
		this.lastOnline = lastOnline;
	}
	public void setTolalPlayTime(String totalPlayTime) {
		this.totalPlayTime = totalPlayTime;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}