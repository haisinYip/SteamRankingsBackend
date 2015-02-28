package com.steamrankings.service.steam;

import java.util.HashMap;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.steamrankings.service.api.profiles.SteamProfile;

public class SteamDataExtractorTest {
    private static SteamApi api = EasyMock.createStrictMock(SteamApi.class);

    private static SteamDataExtractor dataExtractor = null;

    private static final JSONObject TEST_USER_ONE = new JSONObject();
    private static final JSONObject TEST_USER_TWO = new JSONObject();

    private static final String TEST_USER_ONE_STEAM_ID = "1234";
    private static final String TEST_USER_ONE_COMMUNITY_VISIBILITY = "3";
    private static final String TEST_USER_ONE_PROFILE_URL = "http://steamcommunity.com/profiles/" + TEST_USER_ONE_STEAM_ID;
    private static final String TEST_USER_ONE_PERSONA_NAME = "test_user_one";
    private static final String TEST_USER_ONE_REAL_NAME = "Test User One";
    private static final String TEST_USER_ONE_COUNTRY = "Canada";
    private static final String TEST_USER_ONE_PROVINCE = "Quebec";
    private static final String TEST_USER_ONE_CITY = "1";
    private static final String TEST_USER_ONE_AVATAR_FULL = "http://one_full_avatar.jpg";
    private static final String TEST_USER_ONE_AVATAR_MEDIUM = "http://one_medium_avatar.jpg";
    private static final String TEST_USER_ONE_AVATAR_ICON = "http://one_icon_avatar.jpg";
    private static final String TEST_USER_ONE_LAST_LOG_OFF = "659836800";

    private static final String TEST_USER_TWO_STEAM_ID = "5678";
    private static final String TEST_USER_TWO_COMMUNITY_VISIBILITY = "3";
    private static final String TEST_USER_TWO_PROFILE_URL = "http://steamcommunity.com/profiles/TestUserOne/";
    private static final String TEST_USER_TWO_PERSONA_NAME = "test_user_two";
    private static final String TEST_USER_TWO_REAL_NAME = "Test User Two";
    private static final String TEST_USER_TWO_COUNTRY = "Canada";
    private static final String TEST_USER_TWO_PROVINCE = "Quebec";
    private static final String TEST_USER_TWO_CITY = "2";
    private static final String TEST_USER_TWO_AVATAR_FULL = "http://two_full_avatar.jpg";
    private static final String TEST_USER_TWO_AVATAR_MEDIUM = "http://two_medium_avatar.jpg";
    private static final String TEST_USER_TWO_AVATAR_ICON = "http://two_icon_avatar.jpg";
    private static final String TEST_USER_TWO_LAST_LOG_OFF = "662428800";

    @BeforeClass
    public static void setUpBeforeClass() {
        JSONArray players = new JSONArray();
        JSONObject playerData = new JSONObject();

        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_STEAM_ID_KEY, TEST_USER_ONE_STEAM_ID);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_COMMUNITY_VISIBILITY_KEY, TEST_USER_ONE_COMMUNITY_VISIBILITY);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_PROFILE_URL_KEY, TEST_USER_ONE_PROFILE_URL);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_PERSONA_NAME_KEY, TEST_USER_ONE_PERSONA_NAME);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_REAL_NAME_KEY, TEST_USER_ONE_REAL_NAME);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_COUNTRY_KEY, TEST_USER_ONE_COUNTRY);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_PROVINCE_KEY, TEST_USER_ONE_PROVINCE);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_CITY_KEY, TEST_USER_ONE_CITY);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_AVATAR_FULL_URL_KEY, TEST_USER_ONE_AVATAR_FULL);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_AVATAR_MEDIUM_URL_KEY, TEST_USER_ONE_AVATAR_MEDIUM);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_AVATAR_ICON_URL_KEY, TEST_USER_ONE_AVATAR_ICON);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_LAST_LOG_OFF_KEY, TEST_USER_ONE_LAST_LOG_OFF);

        players.put(playerData);
        TEST_USER_ONE.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_RESPONSE_KEY, new JSONObject().put("players", players));

        players = new JSONArray();
        playerData = new JSONObject();

        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_STEAM_ID_KEY, TEST_USER_TWO_STEAM_ID);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_COMMUNITY_VISIBILITY_KEY, TEST_USER_TWO_COMMUNITY_VISIBILITY);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_PROFILE_URL_KEY, TEST_USER_TWO_PROFILE_URL);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_PERSONA_NAME_KEY, TEST_USER_TWO_PERSONA_NAME);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_REAL_NAME_KEY, TEST_USER_TWO_REAL_NAME);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_COUNTRY_KEY, TEST_USER_TWO_COUNTRY);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_PROVINCE_KEY, TEST_USER_TWO_PROVINCE);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_CITY_KEY, TEST_USER_TWO_CITY);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_AVATAR_FULL_URL_KEY, TEST_USER_TWO_AVATAR_FULL);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_AVATAR_MEDIUM_URL_KEY, TEST_USER_TWO_AVATAR_MEDIUM);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_AVATAR_ICON_URL_KEY, TEST_USER_TWO_AVATAR_ICON);
        playerData.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_LAST_LOG_OFF_KEY, TEST_USER_TWO_LAST_LOG_OFF);

        players.put(playerData);
        TEST_USER_TWO.put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_RESPONSE_KEY, new JSONObject().put(SteamDataExtractor.JSON_PLAYER_SUMMARIES_PLAYERS_KEY, players));
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.steam.SteamDataExtractor#SteamDataExtractor(com.steamrankings.service.steam.SteamApi)}
     * .
     */
    @Test
    public void testSteamDataExtractor() {
        dataExtractor = new SteamDataExtractor(api);
        Assert.assertNotNull(dataExtractor);
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.steam.SteamDataExtractor#getSteamProfile(long)}
     * .
     */
    @Test
    public void testGetSteamProfile() {
        dataExtractor = new SteamDataExtractor(api);

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(SteamApi.PARAMETER_STEAM_IDS, TEST_USER_ONE_STEAM_ID);
        EasyMock.expect(api.getJSON(SteamApi.INTERFACE_STEAM_USER, SteamApi.METHOD_GET_PLAYER_SUMMARIES, SteamApi.VERSION_TWO, parameters)).andReturn(TEST_USER_ONE.toString());
        EasyMock.replay(api);

        SteamProfile profile = dataExtractor.getSteamProfile(Long.parseLong(TEST_USER_ONE_STEAM_ID));
        System.out.println(profile.toPrettyString());

        Assert.assertEquals(Long.parseLong(TEST_USER_ONE_STEAM_ID), profile.getSteamId64());
        Assert.assertEquals(TEST_USER_ONE_PROFILE_URL, profile.getSteamCommunityUrl());
        Assert.assertEquals(TEST_USER_ONE_PERSONA_NAME, profile.getPersonaName());
        Assert.assertEquals(TEST_USER_ONE_REAL_NAME, profile.getRealName());
        Assert.assertEquals(TEST_USER_ONE_COUNTRY, profile.getCountryCode());
        Assert.assertEquals(TEST_USER_ONE_PROVINCE, profile.getProvinceCode());
        Assert.assertEquals(TEST_USER_ONE_CITY, profile.getCityCode());
        Assert.assertEquals(TEST_USER_ONE_AVATAR_FULL, profile.getFullAvatarUrl());
        Assert.assertEquals(TEST_USER_ONE_AVATAR_MEDIUM, profile.getMediumAvatarUrl());
        Assert.assertEquals(TEST_USER_ONE_AVATAR_ICON, profile.getIconAvatarUrl());
        Assert.assertEquals(new DateTime(Long.parseLong(TEST_USER_ONE_LAST_LOG_OFF)).getMillisOfSecond(), profile.getLastOnline().getMillisOfSecond());

        EasyMock.verify(api);
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.steam.SteamDataExtractor#getPlayerOwnedGames(long)}
     * .
     */
    @Test
    public void testGetPlayerOwnedGames() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.steam.SteamDataExtractor#getGameAchievementsThreaded(int[])}
     * .
     */
    @Test
    public void testGetGameAchievementsThreaded() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.steam.SteamDataExtractor#getPlayerAchievementsThreaded(long, int[], float[])}
     * .
     */
    @Test
    public void testGetPlayerAchievementsThreaded() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.steam.SteamDataExtractor#convertToSteamId64(java.lang.String)}
     * .
     */
    @Test
    public void testConvertToSteamId64() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.steam.SteamDataExtractor#getSteamId64FromXML(java.lang.String)}
     * .
     */
    @Test
    public void testGetSteamId64FromXML() {
        // fail("Not yet implemented");
    }

}
