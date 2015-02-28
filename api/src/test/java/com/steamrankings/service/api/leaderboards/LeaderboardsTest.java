package com.steamrankings.service.api.leaderboards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMock;
import org.junit.Test;

import com.steamrankings.service.api.APIException;
import com.steamrankings.service.api.SteamRankingsClient;

import junit.framework.TestCase;

public class LeaderboardsTest extends TestCase {
    private static final SteamRankingsClient client = EasyMock.createStrictMock(SteamRankingsClient.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    
    private static final RankEntryByAchievements RANK_ENTRY_ONE = new RankEntryByAchievements(1, 1111, "User1", 3, "100", 1000, "GR");
    private static final RankEntryByAchievements RANK_ENTRY_TWO = new RankEntryByAchievements(2, 2222, "User1", 2, "100", 500, "CY");
    private static final RankEntryByAchievements RANK_ENTRY_THREE = new RankEntryByAchievements(3, 3333, "User1", 1, "100", 250, "CA");
    
    @Test
    public void testGetRanksByAchievementTotal() throws ClientProtocolException, APIException, IOException {
        ArrayList<RankEntryByAchievements> ranks = new ArrayList<RankEntryByAchievements>();
        ranks.add(RANK_ENTRY_ONE);
        ranks.add(RANK_ENTRY_TWO);
        ranks.add(RANK_ENTRY_THREE);
        
        EasyMock.resetToStrict(client);
        EasyMock.expect(client.excecuteRequest("leaderboards?type=achievements&from=0&to=0")).andReturn(mapper.writeValueAsString(ranks));
        EasyMock.replay(client);
        
        List<RankEntryByAchievements> ranksToTest = Leaderboards.getRanksByAchievementTotal(0, 0, client);
        assertEquals(ranks.size(), ranksToTest.size());
        
        for(int i = 0; i < ranks.size(); i++) {
            assertEquals(ranks.get(i).getAchievementsTotal(), ranksToTest.get(i).getAchievementsTotal());
            assertEquals(ranks.get(i).getCompletionRate(), ranksToTest.get(i).getCompletionRate());
            assertEquals(ranks.get(i).getCountryCode(), ranksToTest.get(i).getCountryCode());
            assertEquals(ranks.get(i).getId64(), ranksToTest.get(i).getId64());
            assertEquals(ranks.get(i).getName(), ranksToTest.get(i).getName());
            assertEquals(ranks.get(i).getRankNumber(), ranksToTest.get(i).getRankNumber());
            assertEquals(ranks.get(i).getTotalPlayTime(), ranksToTest.get(i).getTotalPlayTime());
        }
        
        EasyMock.verify(client);
    }
    
    @Test
    public void testGetRanksByTotalPlayTime() throws ClientProtocolException, APIException, IOException {
        ArrayList<RankEntryByAchievements> ranks = new ArrayList<RankEntryByAchievements>();
        ranks.add(RANK_ENTRY_ONE);
        ranks.add(RANK_ENTRY_TWO);
        ranks.add(RANK_ENTRY_THREE);
        
        EasyMock.resetToStrict(client);
        EasyMock.expect(client.excecuteRequest("leaderboards?type=games&from=0&to=0")).andReturn(mapper.writeValueAsString(ranks));
        EasyMock.replay(client);
        
        List<RankEntryByAchievements> ranksToTest = Leaderboards.getRanksByTotalPlayTime(0, 0, client);
        assertEquals(ranks.size(), ranksToTest.size());
        
        for(int i = 0; i < ranks.size(); i++) {
            assertEquals(ranks.get(i).getAchievementsTotal(), ranksToTest.get(i).getAchievementsTotal());
            assertEquals(ranks.get(i).getCompletionRate(), ranksToTest.get(i).getCompletionRate());
            assertEquals(ranks.get(i).getCountryCode(), ranksToTest.get(i).getCountryCode());
            assertEquals(ranks.get(i).getId64(), ranksToTest.get(i).getId64());
            assertEquals(ranks.get(i).getName(), ranksToTest.get(i).getName());
            assertEquals(ranks.get(i).getRankNumber(), ranksToTest.get(i).getRankNumber());
            assertEquals(ranks.get(i).getTotalPlayTime(), ranksToTest.get(i).getTotalPlayTime());
        }
        
        EasyMock.verify(client);
    }
    
    @Test
    public void testGetRanksByCompletionRate() throws ClientProtocolException, APIException, IOException {
        ArrayList<RankEntryByAchievements> ranks = new ArrayList<RankEntryByAchievements>();
        ranks.add(RANK_ENTRY_ONE);
        ranks.add(RANK_ENTRY_TWO);
        ranks.add(RANK_ENTRY_THREE);
        
        EasyMock.resetToStrict(client);
        EasyMock.expect(client.excecuteRequest("leaderboards?type=completionrate&from=0&to=0")).andReturn(mapper.writeValueAsString(ranks));
        EasyMock.replay(client);
        
        List<RankEntryByAchievements> ranksToTest = Leaderboards.getRanksByCompletionRate(0, 0, client);
        assertEquals(ranks.size(), ranksToTest.size());
        
        for(int i = 0; i < ranks.size(); i++) {
            assertEquals(ranks.get(i).getAchievementsTotal(), ranksToTest.get(i).getAchievementsTotal());
            assertEquals(ranks.get(i).getCompletionRate(), ranksToTest.get(i).getCompletionRate());
            assertEquals(ranks.get(i).getCountryCode(), ranksToTest.get(i).getCountryCode());
            assertEquals(ranks.get(i).getId64(), ranksToTest.get(i).getId64());
            assertEquals(ranks.get(i).getName(), ranksToTest.get(i).getName());
            assertEquals(ranks.get(i).getRankNumber(), ranksToTest.get(i).getRankNumber());
            assertEquals(ranks.get(i).getTotalPlayTime(), ranksToTest.get(i).getTotalPlayTime());
        }
        
        EasyMock.verify(client);
    }
}

