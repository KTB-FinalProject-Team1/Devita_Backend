package com.devita.common.constant;

import com.devita.domain.character.enums.MissionType;

public class MissionRewardConstants {
    public static final int USER_TODO_DAILY_LIMIT = 10;
    public static final int DAILY_MISSION_DAILY_LIMIT = 1;
    public static final int FREE_MISSION_DAILY_LIMIT = 3;

    public static int getDailyLimit(MissionType missionType) {
        switch (missionType) {
            case USER_TODO:
                return USER_TODO_DAILY_LIMIT;
            case DAILY_MISSION:
                return DAILY_MISSION_DAILY_LIMIT;
            case FREE_MISSION:
                return FREE_MISSION_DAILY_LIMIT;
            default:
                throw new IllegalArgumentException("Unknown mission type: " + missionType);
        }
    }
}
