package com.steamrankings.service.api.achievements;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

import com.steamrankings.service.api.SteamRankingsClient;

public class AchievementsTest {
	/**
     * Test method for
     * {@link com.steamrankings.service.api.achievements.Achievements#getGameAchievements(int, SteamRankingsClient)}
     * 
     */
    @Test
    public void testGetGameAchievements(){
    	
    	SteamRankingsClient client=new SteamRankingsClient("Test");
        client = EasyMock.createStrictMock(SteamRankingsClient.class);
        String response="{\n\"achievements\":[\n{\"app_id\": 20,\n\"achievement_id\": \"123456\",\n\"unlocked_icon_url\": "
        		+ "\"unlocked/icon/url\",\n\"locked_icon_url\": \"locked/icon/url\",\n\"name\": "
        		+ "\"flames\",\n\"description\": \"here is the desciption\",\n\"unlocked_timestamp\": "
        		+ "1423275897\n},\n{\"app_id\": 20,\n\"achievement_id\": \"123456\",\n\"unlocked_icon_url\": "
        		+ "\"unlocked/icon/url\",\n\"locked_icon_url\": \"locked/icon/url\",\n\"name\": "
        		+ "\"powerups\",\n\"description\": \"here is the desciption\",\n\"unlocked_timestamp\": "
        		+ "1423275897\n}]\n}";
    	try {
			expect(client.excecuteRequest("achievements?appid=20")).andReturn(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client);
        List<GameAchievement> myachievments=Achievements.getGameAchievements(20, client);
        assertNotNull(myachievments);
        assertEquals(myachievments.get(1).getName(),"powerups");
        
        //Testing Id not found
        SteamRankingsClient client2=new SteamRankingsClient("Test");
        client2 = createStrictMock(SteamRankingsClient.class);
    	try {
			expect(client2.excecuteRequest("achievements?appid=30")).andReturn(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client2);
        myachievments=Achievements.getGameAchievements(30, client2);
        assertNull(myachievments);
    }
	/**
     * Test method for
     * {@link com.steamrankings.service.api.achievements.Achievements#getUnlockedAchievements(String, SteamRankingsClient)}
     * 
     */
    @Test
    public void testGeteUnlockedAchievments(){
    	
    	SteamRankingsClient client=new SteamRankingsClient("Test");
        client = EasyMock.createStrictMock(SteamRankingsClient.class);
        String response="[\n{\"app_id\": 20,\n\"achievement_id\": \"123456\",\n\"unlocked_icon_url\": "
        		+ "\"unlocked/icon/url\",\n\"locked_icon_url\": \"locked/icon/url\",\n\"name\": "
        		+ "\"flames\",\n\"description\": \"here is the desciption\",\n\"unlocked_timestamp\": "
        		+ "1423275897\n},\n{\"app_id\": 20,\n\"achievement_id\": \"123456\",\n\"unlocked_icon_url\": "
        		+ "\"unlocked/icon/url\",\n\"locked_icon_url\": \"locked/icon/url\",\n\"name\": "
        		+ "\"powerups\",\n\"description\": \"here is the desciption\",\n\"unlocked_timestamp\": "
        		+ "1423275897}\n]";
        System.out.println(response);
    	try {
			expect(client.excecuteRequest("achievements?id=1233764898948")).andReturn(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client);
        List<GameAchievement> myachievments=Achievements.getUnlockedAchievements("1233764898948", client);
        assertNotNull(myachievments);
        assertEquals(myachievments.get(1).getName(),"powerups");
        
        //Testing Id not found
        SteamRankingsClient client2=new SteamRankingsClient("Test");
        client2 = createStrictMock(SteamRankingsClient.class);
    	try {
			expect(client2.excecuteRequest("achievements?id=1233764898949")).andReturn(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client2);
        myachievments=Achievements.getUnlockedAchievements("1233764898949", client2);
        assertNull(myachievments);
    }
    
	/**
     * Test method for
     * {@link com.steamrankings.service.api.achievements.Achievements#getUnlockedAchievements(String,int, SteamRankingsClient)}
     * 
     */
    @Test
    public void testGetUnlockedAchievments(){
    	
    	SteamRankingsClient client=new SteamRankingsClient("Test");
        client = EasyMock.createStrictMock(SteamRankingsClient.class);
        String response="[\n{\"app_id\": 20,\n\"achievement_id\": \"123456\",\n\"unlocked_icon_url\": "
        		+ "\"unlocked/icon/url\",\n\"locked_icon_url\": \"locked/icon/url\",\n\"name\": "
        		+ "\"flames\",\n\"description\": \"here is the desciption\",\n\"unlocked_timestamp\": "
        		+ "1423275897\n},\n{\"app_id\": 20,\n\"achievement_id\": \"123456\",\n\"unlocked_icon_url\": "
        		+ "\"unlocked/icon/url\",\n\"locked_icon_url\": \"locked/icon/url\",\n\"name\": "
        		+ "\"powerups\",\n\"description\": \"here is the desciption\",\n\"unlocked_timestamp\": "
        		+ "1423275897}\n]";
        System.out.println(response);
    	try {
			expect(client.excecuteRequest("achievements?id=1233764898948&appid=20")).andReturn(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client);
        List<GameAchievement> myachievments=Achievements.getUnlockedAchievements("1233764898948",20, client);
        assertNotNull(myachievments);
        assertEquals(myachievments.get(1).getName(),"powerups");
        
        //Testing appid not found
        SteamRankingsClient client2=new SteamRankingsClient("Test");
        client2 = EasyMock.createStrictMock(SteamRankingsClient.class);
    	try {
			expect(client2.excecuteRequest("achievements?id=1233764898948&appid=30")).andReturn(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        replay(client2);
        myachievments=Achievements.getUnlockedAchievements("1233764898948",30, client2);
        assertNull(myachievments);
    }
    

}
