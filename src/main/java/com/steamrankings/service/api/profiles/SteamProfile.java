package com.steamrankings.service.api.profiles;

import org.joda.time.DateTime;

public class SteamProfile {
    final static public long BASE_ID_64 = 76561197960265728L;
    
    private long id64;
    private String communityID;
    private String personaName;
    private String realName;
    private String countryCode;
    private String totalPlayTime;
    private String fullAvatarUrl;
    private String mediumAvatarUrl;
    private String iconAvatarUrl;
    private DateTime lastOnline;

    public SteamProfile(long id64, String communityID, String personaName, String realName, String countryCode, String fullAvatarUrl, String mediumAvatarUrl, String iconAvatarUrl, DateTime lastOnline) {
        this.id64 = id64;
        this.communityID = communityID;
        this.personaName = personaName;
        this.realName = realName;
        this.countryCode = countryCode;
        this.lastOnline = lastOnline;
        this.fullAvatarUrl = fullAvatarUrl;
        this.mediumAvatarUrl = mediumAvatarUrl;
        this.iconAvatarUrl = iconAvatarUrl;
        this.totalPlayTime = null;
    }
    
    public long getSteamId64() {
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

    public DateTime getLastOnlineTime() {
        return lastOnline;
    }

    public String getFullAvatar() {
        return fullAvatarUrl;
    }

    public String getMediumAvatar() {
        return mediumAvatarUrl;
    }

    public String getIconAvatar() {
        return iconAvatarUrl;
    }
}