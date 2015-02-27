package com.steamrankings.service.api.profiles;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.http.client.ClientProtocolException;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Test;

import com.steamrankings.service.api.APIException;
import com.steamrankings.service.api.SteamRankingsClient;

public class ProfilesTest extends TestCase {
    private static final SteamProfile TEST_PROFILE = new SteamProfile(1234, "TestUser", "TestPersona", "TestName", "TestLand", "TestProvince", "TestCity", "avatar", "avatar", "avatar", new DateTime(
            1424973029));

    /**
     * Test method for
     * {@link com.steamrankings.service.api.profiles.Profiles#getSteamUser(String, SteamRankingsClient)}
     * 
     * @throws APIException
     * @throws IOException
     * @throws ClientProtocolException
     * 
     */
    @Test
    public void testGetSteamUser() throws APIException, ClientProtocolException, IOException {
        SteamRankingsClient client = new SteamRankingsClient(SteamRankingsClient.DEVELOPMENT_ENVIRONMENT);
        client = EasyMock.createStrictMock(SteamRankingsClient.class);

        expect(client.excecuteRequest("profile?id=76561197965726621")).andReturn(TEST_PROFILE.toString());
        replay(client);

        SteamProfile profile = Profiles.getSteamUser("76561197965726621", client);

        assertEquals(TEST_PROFILE.getCityCode(), profile.getCityCode());
        assertEquals(TEST_PROFILE.getCountryCode(), profile.getCountryCode());
        assertEquals(TEST_PROFILE.getFullAvatarUrl(), profile.getFullAvatarUrl());
        assertEquals(TEST_PROFILE.getIconAvatarUrl(), profile.getIconAvatarUrl());
        assertEquals(TEST_PROFILE.getMediumAvatarUrl(), profile.getMediumAvatarUrl());
        assertEquals(TEST_PROFILE.getPersonaName(), profile.getPersonaName());
        assertEquals(TEST_PROFILE.getProvinceCode(), profile.getProvinceCode());
        assertEquals(TEST_PROFILE.getRealName(), profile.getRealName());
        assertEquals(TEST_PROFILE.getSteamCommunityId(), profile.getSteamCommunityId());
        assertEquals(TEST_PROFILE.getSteamCommunityUrl(), profile.getSteamCommunityUrl());
        assertEquals(TEST_PROFILE.getSteamId64(), profile.getSteamId64());
        assertEquals(TEST_PROFILE.getLastOnline().getMillisOfSecond(), profile.getLastOnline().getMillisOfSecond());
    }
}
