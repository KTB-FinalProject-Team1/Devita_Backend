package com.devita.domain.character.domain;

import com.devita.domain.character.enums.RewardType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Reward {
    private RewardType type;
    private int amount;
}