package com.steamrankings.service.api.achievements;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.map.ObjectMapper;
import org.easymock.EasyMock;
import org.junit.Test;

import com.steamrankings.service.api.APIException;
import com.steamrankings.service.api.SteamRankingsClient;

public class AchievementsTest extends TestCase {
    final private static GameAchievement achievementOne = new GameAchievement(1, "One", "Achievement One", "First Achievement", "unlocked url", "locked url");
    final private static GameAchievement achievementTwo = new GameAchievement(1, "Two", "Achievement Two", "Second Achievement", "unlocked url", "locked url");
    final private static GameAchievement achievementThree = new GameAchievement(1, "Three", "Achievement Three", "Third Achievement", "unlocked url", "locked url");
    final private static SteamRankingsClient client = EasyMock.createStrictMock(SteamRankingsClient.class);
    final private static ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void testGetGameAchievements() throws ClientProtocolException, APIException, IOException{
        ArrayList<GameAchievement> achievements = new ArrayList<GameAchievement>();
        achievements.add(achievementOne);
        achievements.add(achievementTwo);
        achievements.add(achievementThree);
        
        EasyMock.resetToStrict(client);
        expect(client.excecuteRequest("achievements?appid=100")).andReturn(mapper.writeValueAsString(achievements));
        replay(client);
        
        List<GameAchievement> achievementsToTest = Achievements.getGameAchievements(100, client);
        assertEquals(achievements.size(), achievementsToTest.size());
        for(int i = 0; i < achievementsToTest.size(); i ++) {
            assertEquals(achievements.get(i).getAchievementId(), achievementsToTest.get(i).getAchievementId());
            assertEquals(achievements.get(i).getAppId(), achievementsToTest.get(i).getAppId());
            assertEquals(achievements.get(i).getDescription(), achievementsToTest.get(i).getDescription());
            assertEquals(achievements.get(i).getLockedIconUrl(), achievementsToTest.get(i).getLockedIconUrl());
            assertEquals(achievements.get(i).getName(), achievementsToTest.get(i).getName());
            assertEquals(achievements.get(i).getUnlockedIconUrl(), achievementsToTest.get(i).getUnlockedIconUrl());
        }
        
        EasyMock.verify(client);
    }
    
    @Test
    public void testGetUnlockedAchievementsForUser() throws ClientProtocolException, APIException, IOException{
        ArrayList<GameAchievement> achievements = new ArrayList<GameAchievement>();
        achievements.add(achievementOne);
        achievements.add(achievementTwo);
        achievements.add(achievementThree);
        
        EasyMock.resetToStrict(client);
        expect(client.excecuteRequest("achievements?id=1234")).andReturn(mapper.writeValueAsString(achievements));
        replay(client);
        
        List<GameAchievement> achievementsToTest = Achievements.getUnlockedAchievements("1234", client);
        assertEquals(achievements.size(), achievementsToTest.size());
        for(int i = 0; i < achievementsToTest.size(); i ++) {
            assertEquals(achievements.get(i).getAchievementId(), achievementsToTest.get(i).getAchievementId());
            assertEquals(achievements.get(i).getAppId(), achievementsToTest.get(i).getAppId());
            assertEquals(achievements.get(i).getDescription(), achievementsToTest.get(i).getDescription());
            assertEquals(achievements.get(i).getLockedIconUrl(), achievementsToTest.get(i).getLockedIconUrl());
            assertEquals(achievements.get(i).getName(), achievementsToTest.get(i).getName());
            assertEquals(achievements.get(i).getUnlockedIconUrl(), achievementsToTest.get(i).getUnlockedIconUrl());
        }
        
        EasyMock.verify(client);
    }
    
    @Test
    public void testGetUnlockedAchievementsForUserAndGame() throws ClientProtocolException, APIException, IOException{
        ArrayList<GameAchievement> achievements = new ArrayList<GameAchievement>();
        achievements.add(achievementOne);
        achievements.add(achievementTwo);
        achievements.add(achievementThree);
        
        EasyMock.resetToStrict(client);
        expect(client.excecuteRequest("achievements?id=1234&appid=100")).andReturn(mapper.writeValueAsString(achievements));
        replay(client);
        
        List<GameAchievement> achievementsToTest = Achievements.getUnlockedAchievements("1234", 100, client);
        assertEquals(achievements.size(), achievementsToTest.size());
        for(int i = 0; i < achievementsToTest.size(); i ++) {
            assertEquals(achievements.get(i).getAchievementId(), achievementsToTest.get(i).getAchievementId());
            assertEquals(achievements.get(i).getAppId(), achievementsToTest.get(i).getAppId());
            assertEquals(achievements.get(i).getDescription(), achievementsToTest.get(i).getDescription());
            assertEquals(achievements.get(i).getLockedIconUrl(), achievementsToTest.get(i).getLockedIconUrl());
            assertEquals(achievements.get(i).getName(), achievementsToTest.get(i).getName());
            assertEquals(achievements.get(i).getUnlockedIconUrl(), achievementsToTest.get(i).getUnlockedIconUrl());
        }
        
        EasyMock.verify(client);
    }
    
    @Test
    public void testGetGameAchievementsException() throws ClientProtocolException, APIException, IOException{
        EasyMock.resetToStrict(client);
        expect(client.excecuteRequest("achievements?appid=100")).andReturn("bad json data");
        replay(client);
        
        assertNull(Achievements.getGameAchievements(100, client));
        
        EasyMock.verify(client);
    }
    
    @Test
    public void testGetUnlockedAchievementsForUserException() throws ClientProtocolException, APIException, IOException{
        EasyMock.resetToStrict(client);
        expect(client.excecuteRequest("achievements?id=1234")).andReturn("bad json data");
        replay(client);
        
        assertNull(Achievements.getUnlockedAchievements("1234", client));
        
        EasyMock.verify(client);
    }
    
    @Test
    public void testGetUnlockedAchievementsForUserAndGameException() throws ClientProtocolException, APIException, IOException{
        EasyMock.resetToStrict(client);
        expect(client.excecuteRequest("achievements?id=1234&appid=100")).andReturn("bad json data");
        replay(client);
        
        assertNull(Achievements.getUnlockedAchievements("1234", 100, client));
        
        EasyMock.verify(client);
    }
}