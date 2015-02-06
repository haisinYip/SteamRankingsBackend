package com.steamrankings.service.core.dataextractors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.joda.time.DateTime;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.community.GameAchievement;
import com.github.koraktor.steamcondenser.steam.community.GameStats;
import com.github.koraktor.steamcondenser.steam.community.SteamGame;
import com.github.koraktor.steamcondenser.steam.community.SteamId;
import com.steamrankings.service.api.profiles.SteamProfile;
import com.steamrankings.service.database.DBConnector;

public class SteamDataExtractor {

    final private static int STEAM_PROFILE_VISIBILITY_PUBLIC = 3;
    private List<SteamId> steamIds;
    private DBConnector db;

    public SteamDataExtractor(List<SteamId> steamIds, DBConnector db) {
        this.steamIds = steamIds;
        this.db = db;
    }

    // Get steam profile from database
    public static List<SteamProfile> getProfileFromDatabase(List<Integer> userIds, DBConnector db) {
        String[] columns = { db.TABLE_PROFILES_COL_ID_LABEL, db.TABLE_PROFILES_COL_COMMUNITY_ID_LABEL, db.TABLE_PROFILES_COL_PERSONA_NAME_LABEL, db.TABLE_PROFILES_COL_REAL_NAME_LABEL,
                db.TABLE_PROFILES_COL_LOC_COUNTRY_LABEL, db.TABLE_PROFILES_COL_LOC_PROVINCE_LABEL, db.TABLE_PROFILES_COL_LOC_CITY_LABEL, db.TABLE_PROFILES_COL_AVATAR_FULL_URL_LABEL,
                db.TABLE_PROFILES_COL_AVATAR_MEDIUM_URL_LABEL, db.TABLE_PROFILES_COL_AVATAR_ICON_URL_LABEL };

        ResultSet results = db.readData(db.TABLE_NAME_PROFILES, columns);

        ArrayList<SteamProfile> profiles = new ArrayList<SteamProfile>();

        try {
            if (results.first() == false) {
                return null;
            }
            while (!results.isAfterLast()) {
                if (userIds.contains(results.getInt(db.TABLE_PROFILES_COL_ID_INDEX))) {
                    profiles.add(new SteamProfile(results.getInt(db.TABLE_PROFILES_COL_ID_INDEX) + SteamProfile.BASE_ID_64, results.getString(db.TABLE_PROFILES_COL_COMMUNITY_ID_INDEX), results
                            .getString(db.TABLE_PROFILES_COL_PERSONA_NAME_INDEX), results.getString(db.TABLE_PROFILES_COL_REAL_NAME_INDEX), results.getString(db.TABLE_PROFILES_COL_LOC_COUNTRY_INDEX),
                            results.getString(db.TABLE_PROFILES_COL_AVATAR_FULL_URL_INDEX), results.getString(db.TABLE_PROFILES_COL_AVATAR_MEDIUM_URL_INDEX), results
                                    .getString(db.TABLE_PROFILES_COL_AVATAR_ICON_URL_INDEX), new DateTime(0)));
                }
                results.next();
            }
        } catch (SQLException e) {
            return null;
        }

        return profiles;
    }

    // Get steam profile from Steam API and add to database
    public static List<SteamProfile> extractProfileFromSteam(List<Integer> userIds, DBConnector db) {
        ArrayList<SteamProfile> profiles = new ArrayList<SteamProfile>();

        for (Integer userId : userIds) {
            SteamId steamId;
            try {
                steamId = SteamId.create(userId + SteamProfile.BASE_ID_64);

                SteamProfile profile = new SteamProfile(steamId.getSteamId64(), steamId.getCustomUrl(), steamId.getNickname(), steamId.getRealName(), steamId.getLocation(),
                        steamId.getAvatarFullUrl(), steamId.getAvatarFullUrl(), steamId.getAvatarIconUrl(), new DateTime(steamId.getMemberSince().getTime()));

                profiles.add(profile);

                String[][] data = { { Integer.toString((int) (profile.getSteamId64() - SteamProfile.BASE_ID_64)), profile.getSteamCommunityId(), profile.getPersonaName(), profile.getRealName(),
                        profile.getCountry(), profile.getCountry(), profile.getCountry(), profile.getFullAvatar(), profile.getMediumAvatar(), profile.getIconAvatar() } };

                db.burstAddToDB(db.TABLE_NAME_PROFILES, data);

            } catch (SteamCondenserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return profiles;
    }

    // Get game info from Steam API and add it to database
    public static void extractAndAddGamesToDatabase(SteamId steamId, DBConnector db) {
        HashMap<Integer, SteamGame> games;
        try {
            games = steamId.getGames();
            for (Entry<Integer, SteamGame> game : games.entrySet()) {
                SteamGame steamGame = game.getValue();
                String[][] data = { { Integer.toString(steamGame.getAppId()), steamGame.getName(), steamGame.getLogoThumbnailUrl(), steamGame.getLogoUrl() } };
                db.burstAddToDB(db.TABLE_NAME_GAMES, data);
                String[] columns = {"id", "name", "icon_url", "logo_url"};
                db.printLastQuery();
                
                extractAndAddGameStatsToDatabase(steamId, steamGame, db);
            }
        } catch (SteamCondenserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void extractAndAddGameStatsToDatabase(SteamId steamId, SteamGame steamGame, DBConnector db) {
        GameStats gameStats;
        try {
            gameStats = steamId.getGameStats(steamGame.getAppId());
            String[][] ownedGame = { { Integer.toString((int) (steamId.getSteamId64() - SteamProfile.BASE_ID_64)), Integer.toString(steamGame.getAppId()), gameStats.getHoursPlayed() } };
            db.burstAddToDB(db.TABLE_NAME_PROFILES_GAMES, ownedGame);
            
            for (GameAchievement achievement : gameStats.getAchievements()) {
                String[][] achievementData = {{"", Integer.toString(steamGame.getAppId()), achievement.getName(), achievement.getDescription(), achievement.getIconOpenURL(), achievement.getIconClosedURL()}};
                db.burstAddToDB(db.TABLE_NAME_ACHIEVEMENTS, achievementData);
                
                if (achievement.isUnlocked()) {
                    String[][] unlockedAchievement = { { Integer.toString((int) (steamId.getSteamId64() - SteamProfile.BASE_ID_64)), "1", Integer.toString(steamGame.getAppId()), achievement.getTimestamp().toString() } };
                    db.burstAddToDB(db.TABLE_NAME_PROFILES_ACHIEVEMENTS, unlockedAchievement);
                }
            }
        } catch (SteamCondenserException e) {
            String[][] ownedGame = { { Integer.toString((int) (steamId.getSteamId64() - SteamProfile.BASE_ID_64)), Integer.toString(steamGame.getAppId()), "0" } };
            db.burstAddToDB(db.TABLE_NAME_PROFILES_GAMES, ownedGame);
        }
    }

    public static SteamId convertToSteamId64(String idToConvert) {
        try {
            long id = Long.parseLong(idToConvert);
            return SteamId.create(id);
        } catch (Exception e) {
            return null;
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static SteamProfile getSteamProfileFromDatabase(long id, ResultSet dbResults) {
        try {
            dbResults.first();
            while (!dbResults.isAfterLast()) {
                if (dbResults.getLong("steam_id_64") == id) {
                    break;
                } else {
                    dbResults.next();
                }
            }

            if (!dbResults.isAfterLast()) {
                return new SteamProfile(dbResults.getLong("steam_id_64"), dbResults.getString("steam_community_id"), dbResults.getString("persona_name"), dbResults.getString("real_name"),
                        dbResults.getString("country_code"), dbResults.getString("avatar_full_url"), dbResults.getString("avatar_medium_url"), dbResults.getString("avatar_icon_url"), new DateTime(0));
            }
        } catch (SQLException e) {
            return null;
        }

        return null;
    }

    public static SteamId getSteamId(long id) {
        try {
            SteamId steamId = SteamId.create(id);

            if (steamId.getVisibilityState() != STEAM_PROFILE_VISIBILITY_PUBLIC) {
                return null;
            }
            return steamId;
        } catch (Exception e) {
            return null;
        }
    }

    public static SteamProfile createSteamProfileFromSteamId(SteamId steamId) {
        SteamProfile steamProfile = new SteamProfile(steamId.getSteamId64(), steamId.getCustomUrl(), steamId.getNickname(), steamId.getRealName(), steamId.getLocation(), steamId.getAvatarFullUrl(),
                steamId.getAvatarFullUrl(), steamId.getAvatarIconUrl(), new DateTime(steamId.getMemberSince().getTime()));

        return steamProfile;
    }

    public void addUsers() {
        for (SteamId steamId : steamIds) {
            SteamProfile profile = createSteamProfileFromSteamId(steamId);
            if (profile != null) {
                String[][] data = { { "0", "0", Long.toString(profile.getSteamId64()), profile.getSteamCommunityId(), profile.getPersonaName(), profile.getRealName(), "CA", profile.getFullAvatar(),
                        profile.getMediumAvatar(), profile.getIconAvatar() } };
                db.writeData("profiles", data);
            }
        }
    }

    public void addGames() {
        for (SteamId steamId : steamIds) {
            try {
                HashMap<Integer, SteamGame> games = steamId.getGames();
                for (Entry<Integer, SteamGame> game : games.entrySet()) {
                    if (game != null) {
                        String[][] data = { { game.getKey().toString(), game.getValue().getName(), game.getValue().getIconUrl(), game.getValue().getLogoUrl() } };
                        db.writeData("games", data);
                    }
                }
            } catch (SteamCondenserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public void addGameAchievements() {
        // TODO Auto-generated method stub

    }
}