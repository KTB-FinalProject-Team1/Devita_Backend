package com.devita.domain.character.enums;

import lombok.Getter;

@Getter
public enum MissionType {
    USER_TODO("USER_TODO", 10),         // 경험치 10
    DAILY_MISSION("DAILY_MISSION", 3),  // 영양제 3개
    FREE_MISSION("FREE_MISSION", 1);    // 영양제 1개

    private final String code;
    private final int rewardAmount;

    MissionType(String code, int rewardAmount) {
        this.code = code;
        this.rewardAmount = rewardAmount;
    }

}