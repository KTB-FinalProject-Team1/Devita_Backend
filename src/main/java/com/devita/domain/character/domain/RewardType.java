package com.devita.domain.character.domain;

import com.devita.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RewardType extends BaseEntity {
    private com.devita.domain.character.enums.RewardType type;
    private int amount;
}