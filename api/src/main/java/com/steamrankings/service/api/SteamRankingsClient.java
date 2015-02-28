package com.steamrankings.service.api;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class SteamRankingsClient {
    public static final String DEVELOPMENT_ENVIRONMENT = "dev";
    public static final String PRODUCTION_ENVIRONMNENT = "prod";

    private String enviroment;
    private String hostName;

    public SteamRankingsClient(String enviroment) throws APIException {
        this.enviroment = enviroment;

        if (this.enviroment.equals(DEVELOPMENT_ENVIRONMENT)) {
            this.hostName = "http://localhost:6789";
        } else if (this.enviroment.equals(PRODUCTION_ENVIRONMNENT)) {
            this.hostName = "http://mikemontreal.ignorelist.com:6789";
        } else {
            throw new APIException("Invalid environment");
        }
    }

    public String excecuteRequest(String requestString) throws APIException, ClientProtocolException, IOException {
        String myRequest = this.hostName + "/" + requestString;
        
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(myRequest);
        HttpResponse response = null;

        response = client.execute(request);
        
        if (response.getStatusLine().getStatusCode() == 400 || response.getStatusLine().getStatusCode() == 404) {
            throw new APIException(EntityUtils.toString(response.getEntity()));
        }
        
        return EntityUtils.toString(response.getEntity());
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getEnviroment() {
        return this.enviroment;
    }
}
