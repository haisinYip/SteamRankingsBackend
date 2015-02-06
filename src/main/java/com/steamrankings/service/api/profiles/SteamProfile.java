package com.steamrankings.service.api.profiles;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

public class SteamProfile {
    final static public long BASE_ID_64 = 76561197960265728L;
    final static public String STEAM_COMMUNITY_BASE_URL = "http://steamcommunity.com/id/";

    @JsonProperty("steam_id64")
    private long id64;

    @JsonProperty("community_id")
    private String communityID;

    @JsonProperty("persona_name")
    private String personaName;

    @JsonProperty("real_name")
    private String realName;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("province_code")
    private String provinceCode;

    @JsonProperty("city_code")
    private String cityCode;

    @JsonProperty("full_avatar_url")
    private String fullAvatarUrl;

    @JsonProperty("medium_avatar_url")
    private String mediumAvatarUrl;

    @JsonProperty("icon_avatar_url")
    private String iconAvatarUrl;

    @JsonProperty("last_online")
    private DateTime lastOnline;

    public SteamProfile() {
    }

    public SteamProfile(long id64, String communityID, String personaName, String realName, String countryCode, String provinceCode, String cityCode, String fullAvatarUrl, String mediumAvatarUrl,
            String iconAvatarUrl, DateTime lastOnline) {
        this.id64 = id64;
        this.communityID = communityID;
        this.personaName = personaName;
        this.realName = realName;
        this.countryCode = countryCode;
        this.provinceCode = provinceCode;
        this.cityCode = cityCode;
        this.fullAvatarUrl = fullAvatarUrl;
        this.mediumAvatarUrl = mediumAvatarUrl;
        this.iconAvatarUrl = iconAvatarUrl;
        this.lastOnline = new DateTime(lastOnline);
    }

    @JsonIgnore
    public long getSteamId64() {
        return id64;
    }

    @JsonIgnore
    public String getSteamCommunityId() {
        return communityID;
    }

    @JsonIgnore
    public String getPersonaName() {
        return personaName;
    }

    @JsonIgnore
    public String getRealName() {
        return realName;
    }

    @JsonIgnore
    public String getCountryCode() {
        return countryCode;
    }

    @JsonIgnore
    public String getProvinceCode() {
        return provinceCode;
    }

    @JsonIgnore
    public String getCityCode() {
        return cityCode;
    }

    @JsonIgnore
    public String getSteamCommunityUrl() {
        return communityID != null ? STEAM_COMMUNITY_BASE_URL + communityID : STEAM_COMMUNITY_BASE_URL + Long.toString(id64);
    }

    @JsonIgnore
    public DateTime getLastOnline() {
        return lastOnline;
    }

    @JsonIgnore
    public String getFullAvatarUrl() {
        return fullAvatarUrl;
    }

    @JsonIgnore
    public String getMediumAvatarUrl() {
        return mediumAvatarUrl;
    }

    @JsonIgnore
    public String getIconAvatarUrl() {
        return iconAvatarUrl;
    }
}