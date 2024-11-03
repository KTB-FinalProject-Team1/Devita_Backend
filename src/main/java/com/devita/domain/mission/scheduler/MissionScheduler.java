package com.devita.domain.mission.scheduler;

import com.devita.common.exception.ErrorCode;
import com.devita.common.exception.ResourceNotFoundException;
import com.devita.domain.category.domain.Category;
import com.devita.domain.category.repository.CategoryRepository;
import com.devita.domain.mission.dto.MissionResDTO;
import com.devita.domain.todo.domain.Todo;
import com.devita.domain.todo.repository.TodoRepository;
import com.devita.domain.user.domain.User;
import com.devita.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
//@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class MissionScheduler {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;
//    private final MissionClient missionClient;

//    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시
    public void createDailyMissions() {
        log.info("미션 생성 시작 시간: {}", LocalDateTime.now());

        List<User> users = userRepository.findAll();

        for (User user : users) {
            try {
                // 해당 사용자의 '강제 미션' 카테고리 찾기
                Category forcedMissionCategory = categoryRepository.findByUserIdAndName(user.getId(), "강제 미션")
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

                // 미션 생성
                Todo mission = new Todo();
                mission.setUser(user);
                mission.setCategory(forcedMissionCategory);  // 강제 미션 카테고리 설정
                // mission.setTitle(missionResponse.getMissionTitle());
                mission.setStatus(false);
                mission.setDate(LocalDate.now());

                todoRepository.save(mission);
                // log.info("사용자 {}의 미션 생성 완료: {}", user.getId(), missionResponse.getMissionTitle());

            } catch (ResourceNotFoundException e) {
                log.error("사용자 {}의 강제 미션 카테고리를 찾을 수 없습니다.", user.getId());
            } catch (Exception e) {
                log.error("사용자 {}의 미션 생성 중 오류 발생: {}", user.getId(), e.getMessage());
            }
        }
    }
}