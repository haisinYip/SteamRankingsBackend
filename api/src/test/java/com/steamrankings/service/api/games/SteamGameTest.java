package com.steamrankings.service.api.games;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

public class SteamGameTest {
	
		final private static int APP_ID = 20;
	    final private static String ICON_URL = "http//test.com/icon_url";
	    final private static String LOGO_URL = "http//test.com/logo_url";
	    final private static String NAME = "GameNameTest";
	    
	    

	    private static SteamGame game;

	    /**
	     * @throws java.lang.Exception
	     */
	    @BeforeClass
	    public static void setUpBeforeClass() throws Exception {
	        game = new SteamGame(APP_ID, ICON_URL, LOGO_URL, NAME);
	    }

	    /**
	     * Test method for
	     * {@link com.steamrankings.service.api.games.SteamGame#SteamGame()}
	     * .
	     */
	    @Test
	    public void testSteamGame() {
	        SteamGame game = new SteamGame();
	        assertNotNull(game);
	    }

	    /**
	     * Test method for
	     * {@link com.steamrankings.service.api.games.SteamGame#SteamGame(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	     * .
	     */
	    @Test
	    public void testSteamProfileLongStringStringStringStringStringStringStringStringStringDateTime() {
	    	SteamGame game = new SteamGame(APP_ID, ICON_URL, LOGO_URL, NAME);
	        assertNotNull(game);
	    }

	    /**
	     * Test method for
	     * {@link com.steamrankings.service.api.games.SteamGame#getAppID()}
	     * .
	     */
	    @Test
	    public void testGetAppId() {
	        assertEquals(APP_ID, game.getAppId());
	    }
	    
	    /**
	     * Test method for
	     * {@link com.steamrankings.service.api.games.SteamGame#getIconUrl()}
	     * .
	     */
	    @Test
	    public void testGetIconUrl() {
	        assertEquals(ICON_URL, game.getIconUrl());
	    }

	    /**
	     * Test method for
	     * {@link com.steamrankings.service.api.games.SteamGame#getLogoUrl()}
	     * .
	     */
	    @Test
	    public void testGetLogoUrl() {
	        assertEquals(LOGO_URL, game.getLogoUrl());
	    }
	    
	    /**
	     * Test method for
	     * {@link com.steamrankings.service.api.games.SteamGame#getName()}
	     * .
	     */
	    @Test
	    public void testGetName() {
	        assertEquals(NAME, game.getName());
	    }
	    /**
	     * Test method for
	     * {@link com.steamrankings.service.api.games.SteamGame#toString()}
	     * .
	     */

	    @Test
	    public void testToString() {
	        System.out.println(game.toString());
	        //fail("Not yet implemented");
	    }

	    /**
	     * Test method for
	     * {@link com.steamrankings.service.api.profiles.SteamProfile#toPrettyString()}
	     * .
	     */
	    @Test
	    public void testToPrettyString() {
	        System.out.println(game.toPrettyString());
	        //fail("Not yet implemented");
	    }


}
