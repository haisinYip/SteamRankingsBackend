package com.steamrankings.service.api.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class SteamRankingsClient {
	
	private String enviroment;
	private String hostName;
	
	public SteamRankingsClient(){
		
	}
	public SteamRankingsClient(String enviroment){
		this.enviroment=enviroment;
		if(this.enviroment=="Dev")
		{
			this.hostName="localhost:8080";
		}
		else if(this.enviroment=="Test")
		{
			this.hostName="localhost:1234";
		}
		else if(this.enviroment=="Production")
		{
			this.hostName="http://mikemontreal.ignorelist.com:6789";
		}
	}
	public String excecuteRequest(String requestString) throws SteamIdException
	{
		String myRequest=this.hostName+"/"+requestString;
		 HttpClient client = new DefaultHttpClient();
	     HttpGet request = new HttpGet(myRequest);
	     HttpResponse response = null;
	     try {
	           response = client.execute(request); 
	           return EntityUtils.toString(response.getEntity());
	        }
	        catch(Exception e){
	        	String[] parts=requestString.split("/");
	        	
	        	if(parts[0]=="profile")
	        	{
	        		throw new SteamIdException("id not found");
	        	}
	        	return null;
	        }
	       
	}
	public String getHostName(){
		return this.hostName;
	}
	public String getEnviroment(){
		return this.enviroment;	
	}

}

