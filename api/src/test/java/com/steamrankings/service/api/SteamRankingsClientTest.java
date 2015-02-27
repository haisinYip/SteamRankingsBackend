package com.steamrankings.service.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SteamRankingsClientTest {
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
        assertEquals("http://localhost:8080", client.getHostName());
    }

    @Test
    public void testProductionEnvironment() throws APIException {
        SteamRankingsClient client = new SteamRankingsClient(SteamRankingsClient.PRODUCTION_ENVIRONMNENT);

        assertNotNull(client);
        assertEquals(SteamRankingsClient.PRODUCTION_ENVIRONMNENT, client.getEnviroment());
        assertEquals("http://mikemontreal.ignorelist.com:6789", client.getHostName());
    }
}
