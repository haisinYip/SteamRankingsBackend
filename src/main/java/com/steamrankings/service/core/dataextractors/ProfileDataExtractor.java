package com.steamrankings.service.core.dataextractors;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.joda.time.DateTime;

import com.github.koraktor.steamcondenser.steam.community.SteamId;
import com.steamrankings.service.api.profiles.SteamProfile;

public class ProfileDataExtractor {

    final private static int STEAM_PROFILE_VISIBILITY_PUBLIC = 3;

    public static SteamProfile getSteamProfile(long id, ResultSet dbResults) {
        try {
            dbResults.first();
            while(!dbResults.isAfterLast()) {
                if(dbResults.getLong("steam_id_64") == id) {
                    break;
                } else {
                    dbResults.next();
                }
            }
            
            if(!dbResults.isAfterLast()) {
                return new SteamProfile(dbResults.getLong("steam_id_64"), dbResults.getString("steam_community_id"), dbResults.getString("persona_name"), dbResults.getString("real_name"), dbResults.getString("country_code"), dbResults.getString("avatar_full_url"), dbResults.getString("avatar_medium_url"), dbResults.getString("avatar_icon_url"), new DateTime(0));
            }
        } catch (SQLException e) {
            return null;
        }
        
        SteamId steamId = getSteamId(id);
        return createSteamProfile(steamId);
    }

    private static SteamId getSteamId(long id) {
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

    private static SteamProfile createSteamProfile(SteamId steamId) {
        if (steamId.getVisibilityState() != STEAM_PROFILE_VISIBILITY_PUBLIC) {
            return null;
        }

        SteamProfile steamProfile = new SteamProfile(steamId.getSteamId64(), steamId.getCustomUrl(), steamId.getNickname(), steamId.getRealName(), steamId.getLocation(),
                steamId.getAvatarFullUrl(), steamId.getAvatarFullUrl(), steamId.getAvatarIconUrl(), new DateTime(steamId.getMemberSince().getTime()));

        System.out.println("Visibility: " + steamId.getVisibilityState());
        System.out.println("Privacy: " + steamId.getPrivacyState());
        System.out.println("getSteamID64: " + steamId.getSteamId64());
        System.out.println("getCustomUrl: " + steamId.getCustomUrl());
        System.out.println("getNickname: " + steamId.getNickname());
        System.out.println("getRealName: " + steamId.getRealName());
        System.out.println("getLocation: " + steamId.getLocation());
        System.out.println("getAvatarFullUrl: " + steamId.getAvatarFullUrl());
        System.out.println("getAvatarIconUrl: " + steamId.getAvatarIconUrl());
        System.out.println("getAvatarMediumUrl: " + steamId.getAvatarMediumUrl());

        return steamProfile;
    }
    
    public static long convertToSteamId64(String idToConvert) {
        try {
            return Long.parseLong(idToConvert);
        } catch (Exception e) {
            return -1;
        }
    }
}