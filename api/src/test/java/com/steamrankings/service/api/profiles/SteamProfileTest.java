/**
 * 
 */
package com.steamrankings.service.api.profiles;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

public class SteamProfileTest {
    final private static long ID_64 = 123456L;
    final private static String COMMUNITY_ID = "TestCommunityID";
    final private static String PERSONA_NAME = "TestPersonaName";
    final private static String REAL_NAME = "TestRealName";
    final private static String COUNTRY_CODE = "CountryCodeTest";
    final private static String PROVINCE_CODE = "ProvinceCodeTest";
    final private static String CITY_CODE = "CityCodeTest";
    final private static String FULL_AVATAR_URL = "http//test.com/test.jpg";
    final private static String MEDIUM_AVATAR_URL = "http//test.com/test.jpg";
    final private static String ICON_AVATAR_URL = "http//test.com/test.jpg";
    final private static String COMMUNITY_PROFILE_URL = SteamProfile.STEAM_COMMUNITY_BASE_URL + ID_64;
    final private static DateTime LAST_ONLINE = new DateTime(123456789);

    private static SteamProfile profile;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        profile = new SteamProfile(ID_64, COMMUNITY_ID, PERSONA_NAME, REAL_NAME, COUNTRY_CODE, PROVINCE_CODE, CITY_CODE, FULL_AVATAR_URL, MEDIUM_AVATAR_URL, ICON_AVATAR_URL, LAST_ONLINE);
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#SteamProfile()}
     * .
     */
    @Test
    public void testSteamProfile() {
        SteamProfile profile = new SteamProfile();
        assertNotNull(profile);
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#SteamProfile(long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.joda.time.DateTime)}
     * .
     */
    @Test
    public void testSteamProfileLongStringStringStringStringStringStringStringStringStringDateTime() {
        SteamProfile profile = new SteamProfile(ID_64, COMMUNITY_ID, PERSONA_NAME, REAL_NAME, COUNTRY_CODE, PROVINCE_CODE, CITY_CODE, FULL_AVATAR_URL, MEDIUM_AVATAR_URL, ICON_AVATAR_URL, LAST_ONLINE);
        assertNotNull(profile);
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#getSteamId64()}
     * .
     */
    @Test
    public void testGetSteamId64() {
        assertEquals(ID_64, profile.getSteamId64());
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#getSteamCommunityId()}
     * .
     */
    @Test
    public void testGetSteamCommunityId() {
        assertEquals(COMMUNITY_ID, profile.getSteamCommunityId());
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#getPersonaName()}
     * .
     */
    @Test
    public void testGetPersonaName() {
        assertEquals(PERSONA_NAME, profile.getPersonaName());
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#getRealName()}
     * .
     */
    @Test
    public void testGetRealName() {
        assertEquals(REAL_NAME, profile.getRealName());
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#getCountryCode()}
     * .
     */
    @Test
    public void testGetCountryCode() {
        assertEquals(COUNTRY_CODE, profile.getCountryCode());
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#getProvinceCode()}
     * .
     */
    @Test
    public void testGetProvinceCode() {
        assertEquals(PROVINCE_CODE, profile.getProvinceCode());
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#getCityCode()}
     * .
     */
    @Test
    public void testGetCityCode() {
        assertEquals(CITY_CODE, profile.getCityCode());
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#getSteamCommunityUrl()}
     * .
     */
    @Test
    public void testGetSteamCommunityUrl() {
        assertEquals(COMMUNITY_PROFILE_URL, profile.getSteamCommunityUrl());
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#getLastOnline()}
     * .
     */
    @Test
    public void testGetLastOnline() {
        assertEquals(LAST_ONLINE, profile.getLastOnline());
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#getFullAvatarUrl()}
     * .
     */
    @Test
    public void testGetFullAvatarUrl() {
        assertEquals(FULL_AVATAR_URL, profile.getFullAvatarUrl());
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#getMediumAvatarUrl()}
     * .
     */
    @Test
    public void testGetMediumAvatarUrl() {
        assertEquals(MEDIUM_AVATAR_URL, profile.getMediumAvatarUrl());
    }

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.SteamProfile#getIconAvatarUrl()}
     * .
     */
    @Test
    public void testGetIconAvatarUrl() {
        assertEquals(ICON_AVATAR_URL, profile.getIconAvatarUrl());
    }
}
