package com.steamrankings.service.api.games;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.steamrankings.service.api.SteamIdException;
import com.steamrankings.service.api.SteamRankingsClient;

public class GamesTest {
	
	/**
     * Test method for
     * {@link com.steamrankings.service.api.games.Games#getSteamGame(int, SteamRankingsClient)}
     * 
     */
    @Test
    public void testGetSteamGame(){
    	
    	SteamRankingsClient client=new SteamRankingsClient("Test");
        client = createStrictMock(SteamRankingsClient.class);
        String response="{\n\"app_id\": 20,\n\"icon_url\": \"icon/url\",\n\"logo_url\": \"logo/url\",\n\"name\": \"GameName\"\n}";
    	try {
			expect(client.excecuteRequest("games?appId=20")).andReturn(response);
		} catch (SteamIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client);
    	SteamGame myGame=Games.getSteamGame(20, client);
        assertNotNull(myGame);
        assertEquals(myGame.getName(),"GameName");
        
        //Testing Id not found
        SteamRankingsClient client2=new SteamRankingsClient("Test");
        client2 = createStrictMock(SteamRankingsClient.class);
    	try {
			expect(client2.excecuteRequest("games?appId=30")).andReturn(null);
		} catch (SteamIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client2);
        myGame=Games.getSteamGame(30, client2);
        assertNull(myGame);
    }
    /**
     * Test method for
     * {@link com.steamrankings.service.api.games.Games#getPlayedSteamGames(String, SteamRankingsClient)}
     * 
     */
    @Test
    public void testGetPlayedSteamGames(){
    	SteamRankingsClient client=new SteamRankingsClient("Test");
        client = createStrictMock(SteamRankingsClient.class);
        String response="[\n{\n\"app_id\": 20,\n\"icon_url\": \"icon/url\",\n\"logo_url\": \"logo/url\",\n\"name\":"
        		+ " \"GameName\"\n},\n{\n\"app_id\": 30,\n\"icon_url\": \"icon/url\",\n\"logo_url\": \"logo/url\",\n\"name\":"
        		+ " \"GameName\"\n}\n]";
    	try {
			expect(client.excecuteRequest("games?id=76561197965726621")).andReturn(response);
		} catch (SteamIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client);
        List<SteamGame> games=Games.getPlayedSteamGames("76561197965726621", client);
        assertNotNull(games);
        assertEquals(games.get(1).getAppId(),30);
        
        //Testing Id not found
        SteamRankingsClient client2=new SteamRankingsClient("Test");
        client2 = createStrictMock(SteamRankingsClient.class);
    	try {
			expect(client2.excecuteRequest("games?id=76561197960435530")).andReturn(null);
		} catch (SteamIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client2);
        games=Games.getPlayedSteamGames("76561197960435530", client2);
        assertNull(games);
    	
    }

}
