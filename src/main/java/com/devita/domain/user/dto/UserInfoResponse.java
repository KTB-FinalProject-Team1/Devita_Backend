package com.devita.domain.user.dto;

import com.devita.domain.character.domain.RewardEntity;
import com.devita.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserInfoResponse {
    private final String nickname;
    private final int experience;
    private final int nutrition;

    public UserInfoResponse(User user, RewardEntity reward) {
        this.nickname = user.getNickname();
        this.experience = reward.getExperience();
        this.nutrition = reward.getNutrition();
    }
}
