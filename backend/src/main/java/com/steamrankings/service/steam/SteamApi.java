package com.steamrankings.service.steam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class SteamApi {
    private String apiKey;

    final public static String INTERFACE_STEAM_USER = "ISteamUser";
    final public static String INTERFACE_STEAM_USER_STATS = "ISteamUserStats";
    final public static String INTERFACE_PLAYER_SERVICE = "IPlayerService";

    final public static String METHOD_GET_PLAYER_SUMMARIES = "GetPlayerSummaries";
    final public static String METHOD_GET_OWNED_GAMES = "GetOwnedGames";
    final public static String METHOD_PLAYER_ACHIEVEMENTS = "GetPlayerAchievements";
    final public static String METHOD_GET_SCHEMA_FOR_GAME = "GetSchemaForGame";

    final public static int VERSION_ONE = 1;
    final public static int VERSION_TWO = 2;

    final public static String PARAMETER_STEAM_IDS = "steamids";
    final public static String PARAMETER_STEAM_ID = "steamid";
    final public static String PARAMETER_APP_ID = "appid";
    final public static String PARAMETER_FORMAT = "format";
    final public static String PARAMETER_LANGUAGE = "l";
    
    final private static int MAX_NUMBER_OF_CONNECTIONS = 25;

    public SteamApi(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getJSON(String apiInterface, String method, int version, Map<String, String> parameters) {
        String url = String.format("https://api.steampowered.com/%s/%s/v%04d/?", apiInterface, method, version);
        parameters.put("key", apiKey);

        boolean first = true;
        for (Entry<String, String> parameter : parameters.entrySet()) {
            if (first) {
                first = false;
            } else {
                url += '&';
            }

            url += String.format("%s=%s", parameter.getKey(), parameter.getValue());
        }

        String data = null;

        try {
        	CloseableHttpClient httpclient = HttpClients.createDefault();
        	HttpGet httpget = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpget);
            
            data = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            return null;
        }

        return data;
    }
    

    public String[] getJSONThreaded(String apiInterface, String method, int version, Map<String, String> parametersConstant, ArrayList<Map<String,String>> parameterList) {
        
    	// Define base URL
    	String url = String.format("https://api.steampowered.com/%s/%s/v%04d/?", apiInterface, method, version);
        parametersConstant.put("key", apiKey);
        
        // Add additional parameters that will remain the same across all requests
        boolean first = true;
        for (Entry<String, String> parameter : parametersConstant.entrySet()) {
            if (first) {
                first = false;
            } else {
                url += '&';
            }

            url += String.format("%s=%s", parameter.getKey(), parameter.getValue());
        }
        url += '&';
        
        // create an array of URIs to perform GETs on
        String[] urisToGet = new String[parameterList.size()];
        
        // Populate array
        int k = 0;
        for (Map<String, String> map : parameterList) {
        	for (Entry<String,String> parameter : map.entrySet()) {
        		urisToGet[k++] = url + String.format("%s=%s", parameter.getKey(), parameter.getValue());
        	}
        }

        // String to return is defined
        String[] data = new String[urisToGet.length];

        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(MAX_NUMBER_OF_CONNECTIONS);
        cm.setDefaultMaxPerRoute(MAX_NUMBER_OF_CONNECTIONS);

        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        
        try {
            
            // create a thread for each URI
            GetThread[] threads = new GetThread[urisToGet.length];
            for (int i = 0; i < threads.length; i++) {
                HttpGet httpget = new HttpGet(urisToGet[i]);
                threads[i] = new GetThread(httpclient, httpget);
            }

            // start the threads
            for (int j = 0; j < threads.length; j++) {
                threads[j].start();
            }

            // join the threads
            for (int j = 0; j < threads.length; j++) {
                threads[j].join();
                data[j] = threads[j].responseString;
            }

        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return data;
    }

    public static String getXML(String communityId) {
        String url = String.format("http://steamcommunity.com/id/%s/?xml=1", communityId);

        String data = null;

        try {
            
        	CloseableHttpClient httpclient = HttpClients.createDefault();
        	HttpGet httpget = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpget);
            data = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            return null;
        }

        return data;
    }
    
    /**
     * A thread that performs a GET.
     */
    static class GetThread extends Thread {

        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final HttpGet httpget;
        public String responseString;

        public GetThread(CloseableHttpClient httpClient, HttpGet httpget) {
            this.httpClient = httpClient;
            this.context = new BasicHttpContext();
            this.httpget = httpget;
        }

        /**
         * Executes the request
         */
        @Override
        public void run() {
            try {
                CloseableHttpResponse response = httpClient.execute(httpget, context);
                try {
                    // get the response
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        responseString = EntityUtils.toString(entity);
                        
                    }
                } finally {
                    response.close();
                }
            } catch (Exception e) {
                System.out.println("HTTP Threaded Get : id" + " - error: " + e);
            }
        }

    }
}
