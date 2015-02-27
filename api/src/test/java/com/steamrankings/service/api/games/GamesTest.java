package com.steamrankings.service.api.games;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMock;
import org.junit.Test;

import com.steamrankings.service.api.APIException;
import com.steamrankings.service.api.SteamRankingsClient;

public class GamesTest extends TestCase {
    final private static ObjectMapper mapper = new ObjectMapper();
    final private static SteamRankingsClient client = EasyMock.createStrictMock(SteamRankingsClient.class);

    final private static SteamGame GAME_ONE = new SteamGame(1, "icon1", "logo1", "Game 1");
    final private static SteamGame GAME_TWO = new SteamGame(2, "icon2", "logo2", "Game 2");

    @Test
    public void testGetSteamGame() throws ClientProtocolException, APIException, IOException {
        EasyMock.resetToStrict(client);
        EasyMock.expect(client.excecuteRequest("games?appId=1")).andReturn(mapper.writeValueAsString(GAME_ONE));
        EasyMock.replay(client);

        SteamGame testGame = Games.getSteamGame(1, client);
        assertNotNull(testGame);

        assertEquals(GAME_ONE.getAppId(), testGame.getAppId());
        assertEquals(GAME_ONE.getIconUrl(), testGame.getIconUrl());
        assertEquals(GAME_ONE.getLogoUrl(), testGame.getLogoUrl());
        assertEquals(GAME_ONE.getName(), testGame.getName());

        EasyMock.verify(client);
    }
    
    @Test
    public void testGetPlayedSteamGames() throws JsonGenerationException, JsonMappingException, ClientProtocolException, APIException, IOException {
        ArrayList<SteamGame> games = new ArrayList<SteamGame>();
        games.add(GAME_ONE);
        games.add(GAME_TWO);
        
        EasyMock.resetToStrict(client);
        EasyMock.expect(client.excecuteRequest("games?id=1234")).andReturn(mapper.writeValueAsString(games));
        EasyMock.replay(client);
        
        List<SteamGame> gamesToTest = Games.getPlayedSteamGames("1234", client);
        
        assertEquals(games.size(), gamesToTest.size());
        
        for(int i = 0; i < games.size(); i++) {
            assertEquals(games.get(i).getAppId(), gamesToTest.get(i).getAppId());
            assertEquals(games.get(i).getIconUrl(), gamesToTest.get(i).getIconUrl());
            assertEquals(games.get(i).getLogoUrl(), gamesToTest.get(i).getLogoUrl());
            assertEquals(games.get(i).getName(), gamesToTest.get(i).getName());
        }
    }
}
