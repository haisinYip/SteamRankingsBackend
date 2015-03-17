package com.steamrankings.service.api.news;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.joda.time.DateTime;

public class SteamNews {

	@JsonProperty("app_id")
	private int appid;

	@JsonProperty("title")
	private String title;

	@JsonProperty("url")
	private String url;

	@JsonProperty("feed_label")
	private String feedLabel;

	@JsonProperty("date")
	private String date;
	
	@JsonProperty("contents")
	private String contents;

	public SteamNews() {
	}

	public SteamNews(int appid, String title, String url) {
		this.appid = appid;
		this.title = title;
		this.url = url;
	}
	
	public SteamNews(int appid, String title, String url, String contents, String date) {
		this.appid = appid;
		this.title = title;
		this.url = url;
		this.contents = contents;
		this.date = date;
	}
	
	public SteamNews(int appid, String title, String url, String date) {
		this.appid = appid;
		this.title = title;
		this.url = url;
		this.date = date;
	}

	@JsonIgnore
	public int getAppId() {
		return appid;
	}

	@JsonIgnore
	public String getTitle() {
		return title;
	}
	
	@JsonIgnore
	public String getUrl() {
		return url;
	}

	@JsonIgnore
	public String getFeedLabel() {
		return feedLabel;
	}

	@JsonIgnore
	public String getDate() {
		return date;
	}
	
	@JsonIgnore
	public String getContents() {
		return contents;
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
