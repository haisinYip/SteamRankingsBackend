package com.steamrankings.service.core.steam;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;

import com.steamrankings.service.api.achievements.GameAchievement;
import com.steamrankings.service.api.games.SteamGame;
import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.service.database.DBConnector;

public class SteamDataDatabase {

    // Get steam profile from database
    public static SteamProfile getProfileFromDatabase(int userIds, DBConnector db) {
        String[] columns = { db.TABLE_PROFILES_COL_ID_LABEL, db.TABLE_PROFILES_COL_COMMUNITY_ID_LABEL, db.TABLE_PROFILES_COL_PERSONA_NAME_LABEL, db.TABLE_PROFILES_COL_REAL_NAME_LABEL,
                db.TABLE_PROFILES_COL_LOC_COUNTRY_LABEL, db.TABLE_PROFILES_COL_LOC_PROVINCE_LABEL, db.TABLE_PROFILES_COL_LOC_CITY_LABEL, db.TABLE_PROFILES_COL_AVATAR_FULL_URL_LABEL,
                db.TABLE_PROFILES_COL_AVATAR_MEDIUM_URL_LABEL, db.TABLE_PROFILES_COL_AVATAR_ICON_URL_LABEL };

        SteamProfile profile = null;
        ResultSet results = db.readData(db.TABLE_NAME_PROFILES, columns);

        try {
            if (results.first() == false) {
                return null;
            }
            while (!results.isAfterLast()) {
                if (userIds == results.getInt(db.TABLE_PROFILES_COL_ID_INDEX)) {
                    profile = new SteamProfile(results.getInt(db.TABLE_PROFILES_COL_ID_INDEX) + SteamProfile.BASE_ID_64, results.getString(db.TABLE_PROFILES_COL_COMMUNITY_ID_INDEX),
                            results.getString(db.TABLE_PROFILES_COL_PERSONA_NAME_INDEX), results.getString(db.TABLE_PROFILES_COL_REAL_NAME_INDEX),
                            results.getString(db.TABLE_PROFILES_COL_LOC_COUNTRY_INDEX), results.getString(db.TABLE_PROFILES_COL_LOC_PROVINCE_INDEX),
                            results.getString(db.TABLE_PROFILES_COL_LOC_CITY_INDEX), results.getString(db.TABLE_PROFILES_COL_AVATAR_FULL_URL_INDEX),
                            results.getString(db.TABLE_PROFILES_COL_AVATAR_MEDIUM_URL_INDEX), results.getString(db.TABLE_PROFILES_COL_AVATAR_ICON_URL_INDEX), new DateTime(0));
                    break;
                }
                results.next();
            }
        } catch (SQLException e) {
            return null;
        }

        return profile;
    }

    // Add steam profile to database
    public static void addProfileToDatabase(DBConnector db, SteamProfile profile) {
        String[][] data = { { Integer.toString((int) (profile.getSteamId64() - SteamProfile.BASE_ID_64)), profile.getSteamCommunityId(), profile.getPersonaName(), profile.getRealName(),
                profile.getCountryCode(), profile.getProvinceCode(), profile.getCityCode(), profile.getFullAvatarUrl(), profile.getMediumAvatarUrl(), profile.getIconAvatarUrl() } };

        db.burstAddToDB(db.TABLE_NAME_PROFILES, data);
    }

    public static void addGamesToDatabase(DBConnector db, SteamProfile profile, Map<SteamGame, Integer> ownedGames) {
        String[][] gamesTableData = new String[ownedGames.size()][4];
        String[][] profilesGamesTableData = new String[ownedGames.size()][3];

        int i = 0;
        for (Entry<SteamGame, Integer> ownedGame : ownedGames.entrySet()) {
            gamesTableData[i][0] = Integer.toString(ownedGame.getKey().getAppId());
            gamesTableData[i][1] = ownedGame.getKey().getName();
            gamesTableData[i][2] = ownedGame.getKey().getIconUrl();
            gamesTableData[i][3] = ownedGame.getKey().getLogoUrl();

            profilesGamesTableData[i][0] = Integer.toString((int) (profile.getSteamId64() - SteamProfile.BASE_ID_64));
            profilesGamesTableData[i][1] = Integer.toString(ownedGame.getKey().getAppId());
            profilesGamesTableData[i][2] = ownedGame.getValue().toString();

            i++;
        }

        db.burstAddToDB(db.TABLE_NAME_GAMES, gamesTableData);
        db.burstAddToDB(db.TABLE_NAME_PROFILES_GAMES, profilesGamesTableData);
    }

    public static void addAchievementsToDatabase(DBConnector db, SteamProfile profile, SteamGame game, List<GameAchievement> gameAchievements, List<GameAchievement> playerAchievements) {
        String[][] achievementsTableData = new String[gameAchievements.size()][6];
        String[][] profilesAchievementsTableData = new String[playerAchievements.size()][4];

        int i = 0;
        for (GameAchievement gameAchievement : gameAchievements) {
            achievementsTableData[i][0] = Integer.toString(gameAchievement.getAchievementId().hashCode());
            achievementsTableData[i][1] = Integer.toString(gameAchievement.getAppId());
            achievementsTableData[i][2] = gameAchievement.getName();
            achievementsTableData[i][3] = gameAchievement.getDescription();
            achievementsTableData[i][4] = gameAchievement.getUnlockedIconUrl();
            achievementsTableData[i][5] = gameAchievement.getLockedIconUrl();

            i++;
        }

        i = 0;
        for (GameAchievement playerAchievement : playerAchievements) {
            profilesAchievementsTableData[i][0] = Integer.toString((int) (profile.getSteamId64() - SteamProfile.BASE_ID_64));
            profilesAchievementsTableData[i][1] = Integer.toString(playerAchievement.getAchievementId().hashCode());
            profilesAchievementsTableData[i][2] = Integer.toString(game.getAppId());
            profilesAchievementsTableData[i][3] = new Timestamp(659836800).toString();
            i++;
        }

        db.burstAddToDB(db.TABLE_NAME_ACHIEVEMENTS, achievementsTableData);
        db.burstAddToDB(db.TABLE_NAME_PROFILES_ACHIEVEMENTS, profilesAchievementsTableData);
    }

    public static long convertToSteamId64(String idToConvert) {
        try {
            return Long.parseLong(idToConvert);
        } catch (Exception e) {
            return -1;
        }
    }
}