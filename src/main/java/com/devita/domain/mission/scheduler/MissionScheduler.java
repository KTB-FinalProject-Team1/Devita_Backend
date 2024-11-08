package com.devita.domain.mission.scheduler;

import com.devita.common.exception.ErrorCode;
import com.devita.common.exception.ResourceNotFoundException;
import com.devita.domain.category.domain.Category;
import com.devita.domain.category.repository.CategoryRepository;
import com.devita.domain.mission.dto.ai.DailyMissionAiResDTO;
import com.devita.domain.mission.service.MissionService;
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
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class MissionScheduler {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;
    private final MissionService missionService;

    @Scheduled(cron = "0 8 16 * * *") // 매일 오전 9시
    public void createDailyMissions() {
        log.info("미션 생성 시작 시간: {}", LocalDateTime.now());

        List<User> userEntities = userRepository.findAll();

        for (User user : userEntities) {
            try {
                // 해당 사용자의 '일일 미션' 카테고리 찾기
                Category dailyMissionCategory = categoryRepository.findByUserIdAndName(user.getId(), "일일 미션")
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

                // AI 서버에 Daily Mission 요청
                DailyMissionAiResDTO missionResponse = missionService.requestDailyMission(user.getId(), "Java");

                // 미션 생성
                Todo mission = new Todo();
                mission.setUser(user);
                mission.setCategory(dailyMissionCategory);
                mission.setTitle(missionResponse.getMissionTitle());
                mission.setStatus(false);
                mission.setDate(LocalDate.now());

                todoRepository.save(mission);
                log.info("사용자 {}의 미션 생성 완료: {}", user.getId(), missionResponse.getMissionTitle());

            } catch (ResourceNotFoundException e) {
                log.error("사용자 {}의 강제 미션 카테고리를 찾을 수 없습니다.", user.getId());
            } catch (Exception e) {
                log.error("사용자 {}의 미션 생성 중 오류 발생: {}", user.getId(), e.getMessage());
            }
        }
    }


}