package com.devita.domain.character.service;

import com.devita.common.exception.AccessDeniedException;
import com.devita.common.exception.ErrorCode;
import com.devita.common.exception.ResourceNotFoundException;
import com.devita.domain.character.domain.RewardEntity;
import com.devita.domain.character.enums.TodoType;
import com.devita.domain.character.domain.Reward;
import com.devita.domain.character.repository.RewardRepository;
import com.devita.domain.todo.domain.Todo;
import com.devita.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {
    private final RedisTemplate<String, Integer> redisTemplate;
    private final RewardRepository rewardRepository;

    private static final int USER_TODO_LIMIT = 300000;
    private static final int DAILY_MISSION_LIMIT = 300000;
    private static final int FREE_MISSION_LIMIT = 300000;
    private static final int NUTRITION_THRESHOLD = 0;

    // 보상 지급 프로세스
    @Transactional
    public void processReward(User user, Todo todo) {
        TodoType todoType = TodoType.fromCategory(todo.getCategory().getName());
        String key = generateKey(user.getId(), todoType);

        if (!canReceiveReward(user.getId(), todoType)) {
            log.warn("{} 해당 유저의 {} 미션 완료 보상 지급 최대 한도 초과", user.getId(), todoType);
            throw new IllegalStateException("일일 보상 한도를 초과했습니다.");
        }

        // Redis 카운트 증가 또는 초기화
        Boolean keyExists = redisTemplate.hasKey(key);
        if (Boolean.FALSE.equals(keyExists)) {
            redisTemplate.opsForValue().set(key, 1, getTimeUntilMidnight(), TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().increment(key);
        }

        // 보상 지급
        RewardEntity rewardEntity = rewardRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    RewardEntity newReward = new RewardEntity(user);
                    return rewardRepository.save(newReward);
                });

        Reward rewardInfo = todoType.getReward();
        switch (rewardInfo.getType()) {
            case EXPERIENCE -> rewardEntity.addExperience(rewardInfo.getAmount());
            case NUTRITION -> rewardEntity.addNutrition(rewardInfo.getAmount());
        }

        rewardRepository.save(rewardEntity);
        log.info("유저 아이디 {}: 보상 타입={}, 수량={}",
                user.getId(), rewardInfo.getType(), rewardInfo.getAmount());
    }


    // 일일 보상 제한 확인
    private boolean canReceiveReward(Long userId, TodoType todoType) {
        String key = generateKey(userId, todoType);
        Integer count = redisTemplate.opsForValue().get(key);

        if (count == null) {
            return true;
        }

        return count < switch (todoType) {
            case USER_TODO -> USER_TODO_LIMIT;
            case DAILY_MISSION -> DAILY_MISSION_LIMIT;
            case FREE_MISSION -> FREE_MISSION_LIMIT;
        };
    }

    //레디스 키 생성
    private String generateKey(Long userId, TodoType todoType) {
        return userId + ":" + todoType.name();
    }

    // 레디스 TTL 설정
    private long getTimeUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return ChronoUnit.SECONDS.between(now, midnight);
    }

    public Long useNutrition(Long userId){
        RewardEntity rewardEntity = rewardRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        if (rewardEntity.getNutrition() <= NUTRITION_THRESHOLD){
            throw new AccessDeniedException(ErrorCode.INSUFFICIENT_SUPPLEMENTS);
        }
        rewardEntity.useNutrition();

        return rewardEntity.getId();
    }
}