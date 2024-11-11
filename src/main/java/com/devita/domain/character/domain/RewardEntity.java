package com.devita.domain.character.domain;

import com.devita.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class RewardEntity {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId  // user_id를 PK로 사용
    @JoinColumn(name = "user_id")
    private User user;

    private int experience;
    private int nutrition;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public RewardEntity(User user) {
        this.user = user;
        this.experience = 0;
        this.nutrition = 0;
    }

    public void addExperience(int amount) {
        this.experience += amount;
    }

    public void addNutrition(int amount) {
        this.nutrition += amount;
    }

    public void useNutrition(){
        this.experience += 10;
        this.nutrition -= 1;
    }
}