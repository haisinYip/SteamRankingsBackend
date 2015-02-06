package com.steamrankings.service.core.dataextractors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.joda.time.DateTime;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
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

    public static long convertToSteamId64(String idToConvert) {
        try {
            return Long.parseLong(idToConvert);
        } catch (Exception e) {
            return -1;
        }
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