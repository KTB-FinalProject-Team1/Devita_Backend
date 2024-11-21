package com.devita.domain.character.enums;

import com.devita.common.constant.CategoryConstants;
import com.devita.domain.character.domain.RewardType;
import lombok.Getter;

@Getter
public enum TodoType {
    USER_TODO(new RewardType(com.devita.domain.character.enums.RewardType.EXPERIENCE, 10)),
    DAILY_MISSION(new RewardType(com.devita.domain.character.enums.RewardType.NUTRITION, 3)),
    FREE_MISSION(new RewardType(com.devita.domain.character.enums.RewardType.NUTRITION, 1));

    private final RewardType rewardType;

    TodoType(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    public static TodoType fromCategory(String categoryName) {
        return switch (categoryName) {
            case CategoryConstants.DAILY_MISSION_CATEGORY -> DAILY_MISSION;
            case CategoryConstants.FREE_MISSION_CATEGORY -> FREE_MISSION;
            default -> USER_TODO;
        };
    }
}
