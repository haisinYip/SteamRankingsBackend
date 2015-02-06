package com.steamrankings.service.api.profiles;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.steamrankings.service.api.leaderboards.Leaderboards;
import com.steamrankings.service.api.leaderboards.RankEntryByAchievements;

import static org.easymock.EasyMock.*;



public class LeaderboardsTest {
	
	final private static int toRank = 100;
	final private static int fromRank = 1;
	//final private static String request = "http://mikemontreal.ignorelist.com:6789/ranksbyachievments?fromrank=" + "1" + "&torank=" + "100";
	
	 List<RankEntryByAchievements> ranksByAchieve = new ArrayList<RankEntryByAchievements>();
	
	  @BeforeClass
	    public static void setUpBeforeClass() throws Exception {
		  
		  
	    }
	  
	  @Test
	  @Ignore
	  public void testgetRanksByAchievementTotals() {
		  HttpClient hc = createMock(HttpClient.class);
		  HttpGet request = createMock(HttpGet.class);
		  try {
			expect(hc.execute(request));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  ranksByAchieve = Leaderboards.getRanksByAchievementTotal(fromRank, toRank);
		  assertNotNull(ranksByAchieve);
		  assertEquals(ranksByAchieve.size(), 100);
		  
	  }

	

}
