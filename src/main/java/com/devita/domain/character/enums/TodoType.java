package com.devita.domain.character.enums;

import com.devita.common.constant.CategoryConstants;
import com.devita.domain.character.domain.Reward;
import lombok.Getter;

@Getter
public enum TodoType {
    USER_TODO(new Reward(RewardType.EXPERIENCE, 10)),
    DAILY_MISSION(new Reward(RewardType.NUTRITION, 3)),
    FREE_MISSION(new Reward(RewardType.NUTRITION, 1));

    private final Reward reward;

    TodoType(Reward reward) {
        this.reward = reward;
    }

    public static TodoType fromCategory(String categoryName) {
        return switch (categoryName) {
            case CategoryConstants.DAILY_MISSION_CATEGORY -> DAILY_MISSION;
            case CategoryConstants.FREE_MISSION_CATEGORY -> FREE_MISSION;
            default -> USER_TODO;
        };
    }
}
