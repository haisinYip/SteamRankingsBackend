package com.steamrankings.service.api.achievements;

import org.joda.time.DateTime;

public abstract class SteamAchievement {
    public abstract int getAppId();

    public abstract String getUnlockedIconUrl();

    public abstract String getLockedIconUrl();

    public abstract String getName();

    public abstract String getDescription();

    public abstract DateTime getTimestamp();
}
