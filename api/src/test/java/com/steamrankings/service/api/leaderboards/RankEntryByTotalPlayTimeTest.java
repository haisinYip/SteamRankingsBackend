package com.steamrankings.service.api.leaderboards;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class RankEntryByTotalPlayTimeTest {
	RankEntryByTotalPlayTime play;
	@Before
	public void setUp() throws Exception {
		play = new RankEntryByTotalPlayTime(1, 1111, "User1", 1000, 3, "100%", "GR");
	}

	@Test
	public void testGetTotalPlayTime() {
		int actual = play.getTotalPlayTime();
		int expected = 1000;
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetCountryCode() {
		String actual = play.getCountryCode();
		String expected = "GR";
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetAchievementsTotal() {
		int actual = play.getAchievementsTotal();
		int expected = 3;
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetCompletionRate() {
		String actual = play.getCompletionRate();
		String expected = "100%";
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetCompletionRateWithoutPercent() {
		float actual = play.getCompletionRateWithoutPercent();
		float expected = 100;
		
		assertEquals(expected, actual,0);
	}
	
	@Test
	public void testToString() {
		String actual = play.toString();
		String expected = "{\"id64\":1111,\"name\":\"User1\",\"rank\":1,\"total_play_time\":1000,\"country_code\":\"GR\",\"achievements_total\":3,\"completion_rate\":\"100%\"}";
		
		assertEquals(expected, actual);
	}

}
