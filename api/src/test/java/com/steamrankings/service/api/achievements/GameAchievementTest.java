package com.steamrankings.service.api.achievements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameAchievementTest {
    final private static int APP_ID = 1234;
    final private static String ACHIEVEMENT_ID = "TEST.ACHIEVEMENT";
    final private static String UNLOCKED_ICON_URL = "http://test.com/test.jpg";
    final private static String LOCKED_ICON_URL = "http://test.com/test.jpg";
    final private static String NAME = "Test";
    final private static String DESCRIPTION = "This is a test achievement";
    final private static DateTime UNLOCKED_TIMESTAMP = new DateTime(123456789);

    private static GameAchievement achievement;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        achievement = new GameAchievement(APP_ID, ACHIEVEMENT_ID, NAME, DESCRIPTION, UNLOCKED_ICON_URL, LOCKED_ICON_URL, new DateTime(123456789));
    }

    @Test
    public void testGameAchievement() {
        assertNotNull(new GameAchievement());
    }

    @Test
    public void testGameAchievementIntString() {
        GameAchievement achievement = new GameAchievement(APP_ID, ACHIEVEMENT_ID);
        assertNotNull(achievement);
        assertEquals(achievement.getAppId(), APP_ID);
        assertEquals(achievement.getAchievementId(), ACHIEVEMENT_ID);
        assertNull(achievement.getDescription());
        assertNull(achievement.getLockedIconUrl());
        assertNull(achievement.getName());
        assertNull(achievement.getUnlockedIconUrl());
        assertNull(achievement.getUnlockedTimestamp());
    }

    @Test
    public void testGameAchievementIntStringString() {
        GameAchievement achievement = new GameAchievement(APP_ID, ACHIEVEMENT_ID, NAME);
        assertNotNull(achievement);
        assertEquals(achievement.getAppId(), APP_ID);
        assertEquals(achievement.getAchievementId(), ACHIEVEMENT_ID);
        assertEquals(achievement.getName(), NAME);
    }
    
    @Test
    public void testGameAchievementIntStringStringStringStringStringDateTime() {
        GameAchievement achievement = new GameAchievement(APP_ID, ACHIEVEMENT_ID, NAME, DESCRIPTION, UNLOCKED_ICON_URL, LOCKED_ICON_URL, new DateTime(123456789));
        assertNotNull(achievement);
        assertEquals(achievement.getAppId(), APP_ID);
        assertEquals(achievement.getAchievementId(), ACHIEVEMENT_ID);
        assertEquals(achievement.getDescription(), DESCRIPTION);
        assertEquals(achievement.getLockedIconUrl(), LOCKED_ICON_URL);
        assertEquals(achievement.getName(), NAME);
        assertEquals(achievement.getUnlockedIconUrl(), UNLOCKED_ICON_URL);
        assertEquals(achievement.getUnlockedTimestamp(), UNLOCKED_TIMESTAMP);
    }
    
    @Test
    public void testGameAchievementIntStringStringStringStringString() {
        GameAchievement achievement = new GameAchievement(APP_ID, ACHIEVEMENT_ID, NAME, DESCRIPTION, UNLOCKED_ICON_URL, LOCKED_ICON_URL);
        assertNotNull(achievement);
        assertEquals(achievement.getAppId(), APP_ID);
        assertEquals(achievement.getAchievementId(), ACHIEVEMENT_ID);
        assertEquals(achievement.getDescription(), DESCRIPTION);
        assertEquals(achievement.getLockedIconUrl(), LOCKED_ICON_URL);
        assertEquals(achievement.getName(), NAME);
        assertEquals(achievement.getUnlockedIconUrl(), UNLOCKED_ICON_URL);
    }

    @Test
    public void testGetAppId() {
        assertEquals(achievement.getAppId(), APP_ID);
    }

    @Test
    public void testGetAchievementId() {
        assertEquals(achievement.getAchievementId(), ACHIEVEMENT_ID);
    }

    @Test
    public void testGetUnlockedIconUrl() {
        assertEquals(achievement.getUnlockedIconUrl(), UNLOCKED_ICON_URL);
    }

    @Test
    public void testGetLockedIconUrl() {
        assertEquals(achievement.getLockedIconUrl(), LOCKED_ICON_URL);
    }

    @Test
    public void testGetName() {
        assertEquals(achievement.getName(), NAME);
    }

    @Test
    public void testGetDescription() {
        assertEquals(achievement.getDescription(), DESCRIPTION);
    }

    @Test
    public void testGetUnlockedTimestamp() {
        assertEquals(achievement.getUnlockedTimestamp(), UNLOCKED_TIMESTAMP);
    }
}
