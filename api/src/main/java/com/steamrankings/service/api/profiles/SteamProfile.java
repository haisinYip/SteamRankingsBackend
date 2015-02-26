package com.steamrankings.service.api.profiles;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.joda.time.DateTime;

public class SteamProfile {
    final static public long BASE_ID_64 = 76561197960265728L;
    final static public String STEAM_COMMUNITY_BASE_URL = "http://steamcommunity.com/profiles/";

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

    @JsonIgnore
    // The "communityvisibilitystate" from Steam; 3 = public, 1 = private
    private int profileState;
    
    public SteamProfile() {
    }

    /**
     * Initializes Steam Profile as public profile with full info
     * @param id64
     * @param communityID
     * @param personaName
     * @param realName
     * @param countryCode
     * @param provinceCode
     * @param cityCode
     * @param fullAvatarUrl
     * @param mediumAvatarUrl
     * @param iconAvatarUrl
     * @param lastOnline
     */
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
        this.profileState = 3;
    }
    
    /**
     * Initializes Steam Profile as a non-public profile with limited info; profileState can be set unlike the other constructor.
     * Generally, this will be for a private profile.
     * @param id64
     * @param communityID
     * @param personaName
     * @param fullAvatarUrl
     * @param mediumAvatarUrl
     * @param iconAvatarUrl
     * @param lastOnline
     * @param profileState
     */
    public SteamProfile(long id64,String communityID, String personaName,String fullAvatarUrl, String mediumAvatarUrl,
            String iconAvatarUrl, DateTime lastOnline, int profileState) {
        this.id64 = id64;
        this.communityID = communityID;
        this.personaName = personaName;
        this.fullAvatarUrl = fullAvatarUrl;
        this.mediumAvatarUrl = mediumAvatarUrl;
        this.iconAvatarUrl = iconAvatarUrl;
        this.lastOnline = new DateTime(lastOnline);
        this.profileState = profileState;
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
        return STEAM_COMMUNITY_BASE_URL + Long.toString(id64);
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

    @JsonIgnore
    public int getProfileState() {
    	return profileState;
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