package com.steamrankings.service.api;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMock;
import org.junit.Test;

import com.steamrankings.service.api.achievements.Achievements;
import com.steamrankings.service.api.achievements.GameAchievement;

public class SteamRankingsClientTest {
	final private static SteamRankingsClient client = EasyMock.createStrictMock(SteamRankingsClient.class);
	final private static ObjectMapper mapper = new ObjectMapper();
	
    @Test
    public void testInvalidEnvironment() throws APIException {
        try {
            new SteamRankingsClient("invalid env");
        } catch (Exception e) {
            assertEquals("Invalid environment", e.getMessage());
        }
    }

    @Test
    public void testDevelopmentEnvironment() throws APIException {
        SteamRankingsClient client = new SteamRankingsClient(SteamRankingsClient.DEVELOPMENT_ENVIRONMENT);

        assertNotNull(client);
        assertEquals(SteamRankingsClient.DEVELOPMENT_ENVIRONMENT, client.getEnviroment());
        assertEquals("http://localhost:6789", client.getHostName());
    }

    @Test
    public void testProductionEnvironment() throws APIException {
        SteamRankingsClient client = new SteamRankingsClient(SteamRankingsClient.PRODUCTION_ENVIRONMNENT);

        assertNotNull(client);
        assertEquals(SteamRankingsClient.PRODUCTION_ENVIRONMNENT, client.getEnviroment());
        assertEquals("http://mikemontreal.ignorelist.com:6789", client.getHostName());
    }
}
