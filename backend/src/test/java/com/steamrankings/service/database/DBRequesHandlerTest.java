package com.steamrankings.service.database;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.service.core.Application;
import com.steamrankings.service.core.RequestHandler;
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.steam.SteamApi;
import com.steamrankings.service.steam.SteamDataDatabase;
import com.steamrankings.service.steam.SteamDataExtractor;

public class DBRequesHandlerTest {
	private Socket socket;
	RequestHandler requestHandler;
	HashMap<String, String> parameters;
	
	@Before
	public void setUp() throws Exception {
		final Properties CONFIG = new Properties();
		parameters = new HashMap<String, String>();
		
		// Load configuration file
		InputStream inputStream = new FileInputStream("config.properties");
		CONFIG.load(inputStream);
		inputStream.close();
		
		// Open socket to receive requests from frontend
		ServerSocket serverSocket = new ServerSocket(Integer.parseInt(CONFIG
				.get("server_port").toString()));
		System.out.println("Backend now running");
		
		
		while (true) {
			// Receive requests and handle them
			socket = serverSocket.accept();

			try {
				System.out.println("New request received");
				requestHandler = new RequestHandler(socket);

			} catch (Exception e) {
				break;
			}
			new Thread(requestHandler).start();
		}
	}
	
	@After 
	public void tearDown() {
		requestHandler.closeDBConnection();
	}

	@Test
	public void testProcessNewUser() {
		parameters.put("id", "nikolaos9029");
		
		long steamId = SteamDataDatabase.convertToSteamId64(parameters.get("id"));
		SteamApi steamApi = new SteamApi(Application.CONFIG.getProperty("apikey"));
		SteamDataExtractor steamDataExtractor = new SteamDataExtractor(steamApi);

		Profile profile = Profile.findById((int) (steamId - SteamProfile.BASE_ID_64));
		SteamProfile steamProfile = null;
		
		if (profile == null) {
			steamProfile = steamDataExtractor.getSteamProfile(steamId);
			
			if (steamProfile == null) {
				fail("Steam user does not exist");
			} else {
				profile = new Profile();
				profile.set("id", (int) (steamProfile.getSteamId64() - SteamProfile.BASE_ID_64));
				profile.set("community_id", steamProfile.getSteamCommunityId());
				profile.set("persona_name", steamProfile.getPersonaName());
				profile.set("real_name", steamProfile.getRealName());
				profile.set("location_country", steamProfile.getCountryCode());
				profile.set("location_province", steamProfile.getProvinceCode());
				profile.set("location_city", steamProfile.getCityCode());
				profile.set("avatar_full_url", steamProfile.getFullAvatarUrl());
				profile.set("avatar_medium_url",
						steamProfile.getMediumAvatarUrl());
				profile.set("avatar_icon_url", steamProfile.getIconAvatarUrl());
				
				profile.set("last_logoff", new Timestamp(steamProfile
						.getLastOnline().getMillis()));
				if (profile.insert())
					System.out.println("Did not save");

				requestHandler.processNewUser(steamDataExtractor, profile, steamProfile);
			}
		}
	}
	
	@Test(expected=IOException.class)
	public void testIndexOutOfBoundsException() throws IOException {
	    requestHandler.processGetProfiles(parameters);
	}

}
