/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steamrankings.service.core;

import com.steamrankings.service.api.ErrorCodes;
import com.steamrankings.service.api.leaderboards.RankEntryByAchievements;
import com.steamrankings.service.api.leaderboards.RankEntryByTotalPlayTime;
import com.steamrankings.service.api.profiles.SteamProfile;
import static com.steamrankings.service.core.ResponseHandler.sendData;
import static com.steamrankings.service.core.ResponseHandler.sendError;
import com.steamrankings.service.database.Database;
import com.steamrankings.service.models.Profile;
import com.steamrankings.service.models.ProfilesAchievements;
import com.steamrankings.service.models.ProfilesGames;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *
 * @author Michael
 */
public class LeaderboardHandler extends AbstractHandler {

    private final static Logger LOGGER = Logger.getLogger(LeaderboardHandler.class.getName());

    public static final String PARAMETER_LEADERBOARD_TYPE = "type";
    public static final String PARAMETER_TO_RANK = "to";
    public static final String PARAMETER_FROM_RANK = "from";
    public static final String PARAMETER_GAME_ID = "gid";
    public static final String PARAMETER_COUNTRY_ID = "id";

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // Open DB connection
        Database.openDBConnection();

        Map<String, String[]> param = request.getParameterMap();

        // Check to see if parameters are correct
        if (param == null || param.isEmpty() || !param.containsKey(PARAMETER_LEADERBOARD_TYPE) || !param.containsKey(PARAMETER_TO_RANK)
                || !param.containsKey(PARAMETER_FROM_RANK)) {
            sendError("Invalid parameters", response, baseRequest);
            Database.closeDBConnection();
            return;
        }

        switch (param.get(PARAMETER_LEADERBOARD_TYPE)[0]) {
            case "achievements": {
                ArrayList<RankEntryByAchievements> leaderboard = processGetAchievementLeaderboard(param.get(PARAMETER_TO_RANK)[0], param.get(PARAMETER_FROM_RANK)[0]);
                checkAndSendResponse(leaderboard, response, baseRequest);
                break;
            }
            case "games": {
                ArrayList<RankEntryByAchievements> leaderboard = processGetGamesLeaderboard(param.get(PARAMETER_TO_RANK)[0], param.get(PARAMETER_FROM_RANK)[0], param.get(PARAMETER_GAME_ID)[0]);
                checkAndSendResponse(leaderboard, response, baseRequest);
                break;
            }
            case "completionrate": {
                ArrayList<RankEntryByTotalPlayTime> leaderboard = processGetCompletionRateLeaderboard(param.get(PARAMETER_TO_RANK)[0], param.get(PARAMETER_FROM_RANK)[0]);
                checkAndSendResponse(leaderboard, response, baseRequest);
                break;
            }
            case "playtime": {
                ArrayList<RankEntryByTotalPlayTime> leaderboard = processGetTotalPlayTimeLeaderboard(param.get(PARAMETER_TO_RANK)[0], param.get(PARAMETER_FROM_RANK)[0]);
                checkAndSendResponse(leaderboard, response, baseRequest);
                break;
            }
            case "countries": {
                ArrayList<RankEntryByAchievements> leaderboard = processGetCountryLeaderboard(param.get(PARAMETER_TO_RANK)[0], param.get(PARAMETER_FROM_RANK)[0], param.get(PARAMETER_COUNTRY_ID)[0]);
                checkAndSendResponse(leaderboard, response, baseRequest);
                break;
            }
            default:
                sendError(ErrorCodes.API_ERROR_BAD_ARGUMENTS, response, baseRequest);
                Database.closeDBConnection();
                break;
        }

    }

    private ArrayList<RankEntryByAchievements> processGetGamesLeaderboard(String toRank, String fromRank, String gameId) {

        int from = Integer.parseInt(fromRank);
        int to = Integer.parseInt(toRank);
        if (from > to) {
            return null;
        }

        List<ProfilesGames> profileGames = ProfilesGames.where("game_id = ?", gameId);
        ArrayList<Profile> listProfiles = new ArrayList<>();

        for (ProfilesGames profileGame : profileGames) {
            listProfiles.add(Profile.findById(profileGame.get("profile_id")));
        }

        ArrayList<Profile> profiles = new ArrayList<>(listProfiles);
        HashMap<Profile, List<Integer>> profileAchievementCounts = new HashMap<>();

        for (Profile profile : profiles) {
            List<Integer> details = new ArrayList<>();
            details.add(ProfilesAchievements.where("profile_id = ? and game_id = ?", profile.getInteger("id"), gameId).size());
            details.add(getTotalPlayTime(profile));

            profileAchievementCounts.put(profile, details);
        }

        int i = 1;
        ArrayList<RankEntryByAchievements> rankEntries = new ArrayList<>();
        for (Entry<Profile, List<Integer>> profileAchievementCount : profileAchievementCounts.entrySet()) {
            rankEntries.add(new RankEntryByAchievements(i, profileAchievementCount.getKey().getInteger("id") + SteamProfile.BASE_ID_64, profileAchievementCount.getKey().getString("persona_name"),
                    profileAchievementCount.getValue().get(0), profileAchievementCount.getKey().getFloat("avg_completion_rate").toString() + '%', profileAchievementCount.getValue().get(1),
                    profileAchievementCount.getKey().getString("location_country")));
        }

        Collections.sort(rankEntries, new Comparator<RankEntryByAchievements>() {
            @Override
            public int compare(RankEntryByAchievements o1, RankEntryByAchievements o2) {
                return o2.getAchievementsTotal() - o1.getAchievementsTotal();
            }
        });

        for (RankEntryByAchievements rank : rankEntries) {
            rankEntries.get(i - 1).setRankNumber(i);
            i++;
        }

        return rankEntries;
    }

    private ArrayList<RankEntryByAchievements> processGetAchievementLeaderboard(String toRank, String fromRank) {
        int from = Integer.parseInt(fromRank);
        int to = Integer.parseInt(toRank);
        if (from > to) {
            return null;
        }

        HashMap<Profile, List<Integer>> profileAchievementCounts = getAchievementsAndTotalPlayTime();

        int i = 1;
        ArrayList<RankEntryByAchievements> rankEntries = new ArrayList<RankEntryByAchievements>();
        for (Entry<Profile, List<Integer>> profileAchievementCount : profileAchievementCounts.entrySet()) {
            rankEntries.add(new RankEntryByAchievements(i, profileAchievementCount.getKey().getInteger("id") + SteamProfile.BASE_ID_64, profileAchievementCount.getKey().getString("persona_name"),
                    profileAchievementCount.getValue().get(0), profileAchievementCount.getKey().getFloat("avg_completion_rate").toString() + '%', profileAchievementCount.getValue().get(1),
                    profileAchievementCount.getKey().getString("location_country")));
        }

        Collections.sort(rankEntries, new Comparator<RankEntryByAchievements>() {
            public int compare(RankEntryByAchievements o1, RankEntryByAchievements o2) {
                return o2.getAchievementsTotal() - o1.getAchievementsTotal();
            }
        });

        for (RankEntryByAchievements rank : rankEntries) {
            rankEntries.get(i - 1).setRankNumber(i);
            i++;
        }

        return rankEntries;
    }

    private ArrayList<RankEntryByTotalPlayTime> processGetTotalPlayTimeLeaderboard(String toRank, String fromRank) {

        int from = Integer.parseInt(fromRank);
        int to = Integer.parseInt(toRank);
        if (from > to) {
            return null;
        }

        // make rank entries based off total_play_time in
        // profileTotalPlayTimeCounts
        HashMap<Profile, List<Integer>> profileTotalPlayTimeCounts = getAchievementsAndTotalPlayTime();

        int i = 1;
        ArrayList<RankEntryByTotalPlayTime> rankEntries = new ArrayList<RankEntryByTotalPlayTime>();
        for (Entry<Profile, List<Integer>> profileTotalPlayTime : profileTotalPlayTimeCounts.entrySet()) {
            rankEntries.add(new RankEntryByTotalPlayTime(i, profileTotalPlayTime.getKey().getInteger("id") + SteamProfile.BASE_ID_64, profileTotalPlayTime.getKey().getString("persona_name"),
                    profileTotalPlayTime.getValue().get(1), profileTotalPlayTime.getValue().get(0), profileTotalPlayTime.getKey().getFloat("avg_completion_rate").toString() + '%',
                    profileTotalPlayTime.getKey().getString("location_country")));
        }
        // sort rank entries by total_play_time
        Collections.sort(rankEntries, new Comparator<RankEntryByTotalPlayTime>() {
            public int compare(RankEntryByTotalPlayTime o1, RankEntryByTotalPlayTime o2) {
                return o2.getTotalPlayTime() - o1.getTotalPlayTime();
            }
        });
        for (RankEntryByTotalPlayTime rank : rankEntries) {
            rankEntries.get(i - 1).setRankNumber(i);
            i++;
        }
        return rankEntries;
    }

    private ArrayList<RankEntryByTotalPlayTime> processGetCompletionRateLeaderboard(String toRank, String fromRank) {

        int from = Integer.parseInt(fromRank);
        int to = Integer.parseInt(toRank);
        if (from > to) {
            return null;
        }

        HashMap<Profile, List<Integer>> profileTotalPlayTimeCounts = getAchievementsAndTotalPlayTime();

        // make rankentries based off total play time in
        // profileTotalPlayTimeCounts
        int i = 1;
        ArrayList<RankEntryByTotalPlayTime> rankEntries = new ArrayList<>();

        for (Entry<Profile, List<Integer>> profileTotalPlayTime : profileTotalPlayTimeCounts.entrySet()) {
            rankEntries.add(new RankEntryByTotalPlayTime(i, profileTotalPlayTime.getKey().getInteger("id") + SteamProfile.BASE_ID_64, profileTotalPlayTime.getKey().getString("persona_name"),
                    profileTotalPlayTime.getValue().get(1), profileTotalPlayTime.getValue().get(0), profileTotalPlayTime.getKey().getFloat("avg_completion_rate").toString() + '%',
                    profileTotalPlayTime.getKey().getString("location_country")));
        }

        // sort rankentries by completion rate
        Collections.sort(rankEntries, new Comparator<RankEntryByTotalPlayTime>() {
            @Override
            public int compare(RankEntryByTotalPlayTime o1, RankEntryByTotalPlayTime o2) {
                return Float.compare(o2.getCompletionRateWithoutPercent(), o1.getCompletionRateWithoutPercent());
            }
        });
        for (RankEntryByTotalPlayTime rank : rankEntries) {
            rankEntries.get(i - 1).setRankNumber(i);
            i++;
        }
        return rankEntries;
    }

    private ArrayList<RankEntryByAchievements> processGetCountryLeaderboard(String toRank, String fromRank, String countryCode) {

        int from = Integer.parseInt(fromRank);
        int to = Integer.parseInt(toRank);
        if (from > to) {
            return null;
        }

        ArrayList<Integer> indicesToDelete = new ArrayList<>();
        ArrayList<RankEntryByAchievements> rankEntries = processGetAchievementLeaderboard(fromRank, toRank);
        ArrayList<RankEntryByAchievements> updatedRankEntries = new ArrayList<>();
        for (int i = 0; i < rankEntries.size(); i++) {
            if (rankEntries.get(i).getCountryCode() == null || !rankEntries.get(i).getCountryCode().equals(countryCode)) {
                indicesToDelete.add(i);
            }
        }

        for (int j = 0; j < rankEntries.size(); j++) {
            if (!indicesToDelete.contains(j)) {
                updatedRankEntries.add(rankEntries.get(j));
            }
        }

        return updatedRankEntries;
    }

    private int getTotalPlayTime(Profile profile) {
        int sum = 0;

        // get all games of profile
        List<ProfilesGames> profileGames = ProfilesGames.where("profile_id = ?", profile.getInteger("id"));
        ArrayList<ProfilesGames> games = new ArrayList<>(profileGames);

        // get total_play_time of each game and sum
        // possibly can optimize? Nested for loop may give slow response time
        for (ProfilesGames game : games) {
            sum += game.getInteger("total_play_time");
        }
        return sum;
    }

    private HashMap<Profile, List<Integer>> getAchievementsAndTotalPlayTime() {
        List<Profile> listProfiles = Profile.findAll();
        ArrayList<Profile> profiles = new ArrayList<>(listProfiles);
        HashMap<Profile, List<Integer>> profileAchievementCounts = new HashMap<>();
        for (Profile profile : profiles) {
            List<Integer> details = new ArrayList<>();
            details.add(ProfilesAchievements.where("profile_id = ?", profile.getInteger("id")).size());
            details.add(getTotalPlayTime(profile));

            profileAchievementCounts.put(profile, details);
        }

        return profileAchievementCounts;
    }

    private void checkAndSendResponse(ArrayList<?> leaderboard, HttpServletResponse response, Request baseRequest) throws IOException {
        if (leaderboard == null) {
            sendError("Something went wrong", response, baseRequest);
        } else {
            sendData(leaderboard.toString(), response, baseRequest);
        }
        Database.closeDBConnection();
    }
}
