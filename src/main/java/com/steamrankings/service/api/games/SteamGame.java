package com.steamrankings.service.api.games;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class SteamGame {

	@JsonProperty("app_id")
    private int appid;
	
	 @JsonProperty("icon_url")
    private String iconUrl;
	 
	 @JsonProperty("logo_url")
    private String logoUrl;
	 
	 @JsonProperty("name")
    private String name;

	public SteamGame(){
		
	}
	
    public SteamGame(int appid, String iconUrl, String logoUrl, String name) {
        this.appid = appid;
        this.iconUrl = iconUrl;
        this.logoUrl = logoUrl;
        this.name = name;
    }

    @JsonIgnore
    public int getAppId() {
        return appid;
    }

    @JsonIgnore
    public String getIconUrl() {
        return iconUrl;
    }

    @JsonIgnore
    public String getLogoUrl() {
        return logoUrl;
    }

    @JsonIgnore
    public String getName() {
        return name;
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