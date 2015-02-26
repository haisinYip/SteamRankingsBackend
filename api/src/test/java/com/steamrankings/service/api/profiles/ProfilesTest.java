package com.steamrankings.service.api.profiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.steamrankings.service.api.client.SteamIdException;
import com.steamrankings.service.api.client.SteamRankingsClient;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.eq;

public class ProfilesTest extends TestCase {
	

	
    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.Profiles#getSteamUser(String, SteamRankingsClient)}
     * 
     */
    @Test
    public void testGetSteamUser(){
    	
    	SteamRankingsClient client=new SteamRankingsClient("Test");
        client = createStrictMock(SteamRankingsClient.class);
        String response="{\n\"steam_id64\": 76561197965726621,\n\"community_id\": \"12345\",\n\"persona_name\":"
        				+ " \"Robin\",\n\"real_name\": \"Mike\",\n\"country_code\": \"US\",\n\"province_code\": "
        				+ "\"MA\",\n\"city_code\": \"Boston\",\n\"full_avatar_url\": \"Full/avatar/url\",\n\"medium_avatar_url\":"
        				+ " \"Medium/avatar/url\",\n\"icon_avatar_url\": \"Icon/avatar/url\",\n\"last_online\": 1423275897\n}";
    	try {
			expect(client.excecuteRequest("profile?id=76561197965726621")).andReturn(response);
		} catch (SteamIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client);
    	SteamProfile myprofile=Profiles.getSteamUser("76561197965726621", client);
        assertNotNull(myprofile);
        assertEquals(myprofile.getCityCode(),"Boston");
        
        //Testing Id not found
        SteamRankingsClient client2=new SteamRankingsClient("Test");
        client2 = createStrictMock(SteamRankingsClient.class);
    	try {
			expect(client2.excecuteRequest("profile?id=76561197960435530")).andReturn(null);
		} catch (SteamIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client2);
        myprofile=Profiles.getSteamUser("76561197960435530", client2);
        assertNull(myprofile);
    }
    
    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.Profiles#getSteamFriends(String, SteamRankingsClient)}
     * 
     */
    @Test
    public void testGetSteamFriends(){
    	SteamRankingsClient client=new SteamRankingsClient("Test");
        client = createStrictMock(SteamRankingsClient.class);
        String response="[\n{\n\"steam_id64\": 76561197965726621,\n\"community_id\": \"12345\",\n\"persona_name\":"
        				+ " \"Robin\",\n\"real_name\": \"Mike\",\n\"country_code\": \"US\",\n\"province_code\": "
        				+ "\"MA\",\n\"city_code\": \"Boston\",\n\"full_avatar_url\": \"Full/avatar/url\",\n\"medium_avatar_url\":"
        				+ " \"Medium/avatar/url\",\n\"icon_avatar_url\": \"Icon/avatar/url\",\n\"last_online\": 1423275897\n}"
        				+ ",\n{\n\"steam_id64\": 76561197965726621,\n\"community_id\": \"12345\",\n\"persona_name\":"
        				+ " \"Robin\",\n\"real_name\": \"Samy\",\n\"country_code\": \"US\",\n\"province_code\": "
        				+ "\"MA\",\n\"city_code\": \"Boston\",\n\"full_avatar_url\": \"Full/avatar/url\",\n\"medium_avatar_url\":"
        				+ " \"Medium/avatar/url\",\n\"icon_avatar_url\": \"Icon/avatar/url\",\n\"last_online\": 1423275897\n}\n]";
    	try {
			expect(client.excecuteRequest("friends?id=76561197965726621")).andReturn(response);
		} catch (SteamIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client);
        List<SteamProfile> friends=Profiles.getSteamFriends("76561197965726621", client);
        assertNotNull(friends);
        assertEquals(friends.get(0).getCityCode(),"Boston");
        
        //Testing Id not found
        SteamRankingsClient client2=new SteamRankingsClient("Test");
        client2 = createStrictMock(SteamRankingsClient.class);
    	try {
			expect(client2.excecuteRequest("friends?id=76561197960435530")).andReturn(null);
		} catch (SteamIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client2);
        friends=Profiles.getSteamFriends("76561197960435530", client2);
        assertNull(friends);
    	
    }

}
