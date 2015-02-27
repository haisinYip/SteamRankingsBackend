package com.steamrankings.service.api.profiles;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Test;

import com.steamrankings.service.api.APIException;
import com.steamrankings.service.api.SteamRankingsClient;

public class ProfilesTest extends TestCase {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final SteamRankingsClient client = EasyMock.createStrictMock(SteamRankingsClient.class);

    private static final SteamProfile PROFILE_ONE = new SteamProfile(1234, "TestUser1", "TestPersona1", "TestName1", "TestLand1", "TestProvince1", "TestCity1", "avatar1", "avatar1", "avatar1",
            new DateTime(1124973029));
    private static final SteamProfile PROFILE_TWO = new SteamProfile(1234, "TestUser2", "TestPersona2", "TestName2", "TestLand2", "TestProvince2", "TestCity2", "avatar2", "avatar2", "avatar2",
            new DateTime(1224973029));

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
        EasyMock.resetToStrict(client);
        expect(client.excecuteRequest("profile?id=76561197965726621")).andReturn(PROFILE_ONE.toString());
        replay(client);

        SteamProfile profile = Profiles.getSteamUser("76561197965726621", client);

        assertEquals(PROFILE_ONE.getCityCode(), profile.getCityCode());
        assertEquals(PROFILE_ONE.getCountryCode(), profile.getCountryCode());
        assertEquals(PROFILE_ONE.getFullAvatarUrl(), profile.getFullAvatarUrl());
        assertEquals(PROFILE_ONE.getIconAvatarUrl(), profile.getIconAvatarUrl());
        assertEquals(PROFILE_ONE.getMediumAvatarUrl(), profile.getMediumAvatarUrl());
        assertEquals(PROFILE_ONE.getPersonaName(), profile.getPersonaName());
        assertEquals(PROFILE_ONE.getProvinceCode(), profile.getProvinceCode());
        assertEquals(PROFILE_ONE.getRealName(), profile.getRealName());
        assertEquals(PROFILE_ONE.getSteamCommunityId(), profile.getSteamCommunityId());
        assertEquals(PROFILE_ONE.getSteamCommunityUrl(), profile.getSteamCommunityUrl());
        assertEquals(PROFILE_ONE.getSteamId64(), profile.getSteamId64());
        assertEquals(PROFILE_ONE.getLastOnline().getMillisOfSecond(), profile.getLastOnline().getMillisOfSecond());

        EasyMock.verify(client);
    }

    @Test
    public void testGetSteamFriends() throws APIException, ClientProtocolException, IOException {
        ArrayList<SteamProfile> friends = new ArrayList<SteamProfile>();
        friends.add(PROFILE_ONE);
        friends.add(PROFILE_TWO);

        EasyMock.resetToStrict(client);
        expect(client.excecuteRequest("friends?id=76561197965726621")).andReturn(mapper.writeValueAsString(friends));
        replay(client);

        List<SteamProfile> friendsToTest = Profiles.getSteamFriends("76561197965726621", client);
        assertEquals(friends.size(), friendsToTest.size());
        for (int i = 0; i < friends.size(); i++) {
            assertEquals(friends.get(i).getCityCode(), friendsToTest.get(i).getCityCode());
            assertEquals(friends.get(i).getCountryCode(), friendsToTest.get(i).getCountryCode());
            assertEquals(friends.get(i).getFullAvatarUrl(), friendsToTest.get(i).getFullAvatarUrl());
            assertEquals(friends.get(i).getIconAvatarUrl(), friendsToTest.get(i).getIconAvatarUrl());
            assertEquals(friends.get(i).getMediumAvatarUrl(), friendsToTest.get(i).getMediumAvatarUrl());
            assertEquals(friends.get(i).getPersonaName(), friendsToTest.get(i).getPersonaName());
            assertEquals(friends.get(i).getProvinceCode(), friendsToTest.get(i).getProvinceCode());
            assertEquals(friends.get(i).getRealName(), friendsToTest.get(i).getRealName());
            assertEquals(friends.get(i).getSteamCommunityId(), friendsToTest.get(i).getSteamCommunityId());
            assertEquals(friends.get(i).getSteamCommunityUrl(), friendsToTest.get(i).getSteamCommunityUrl());
            assertEquals(friends.get(i).getSteamId64(), friendsToTest.get(i).getSteamId64());
            assertEquals(friends.get(i).getLastOnline().getMillisOfSecond(), friendsToTest.get(i).getLastOnline().getMillisOfSecond());
        }

        EasyMock.verify(client);
    }
    
    @Test
    public void testGetSteamFriendsException() throws APIException, ClientProtocolException, IOException {
        ArrayList<SteamProfile> friends = new ArrayList<SteamProfile>();
        friends.add(PROFILE_ONE);
        friends.add(PROFILE_TWO);

        EasyMock.resetToStrict(client);
        expect(client.excecuteRequest("friends?id=76561197965726621")).andReturn("bad json data");
        replay(client);

        assertNull(Profiles.getSteamFriends("76561197965726621", client));
        EasyMock.verify(client);
    }
}
