package com.devita.domain.mission.controller;


import com.devita.common.response.ApiResponse;
import com.devita.domain.mission.dto.ai.MissionAiResDTO;
import com.devita.domain.mission.dto.client.FreeMissionReqDTO;
import com.devita.domain.mission.service.MissionService;
import com.devita.domain.todo.domain.Todo;
import com.devita.domain.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mission")
public class MissionController {
    private final MissionService missionService;
    private final TodoRepository todoRepository;

    @GetMapping("/daily")
    public ApiResponse<Todo> getDailyMission(@AuthenticationPrincipal Long userId){
        Todo dailyMission = todoRepository.findTodosByUserIdAndCategoryNameAndDate(userId, "일일 미션", LocalDate.now());

        return ApiResponse.success(dailyMission);

    }

    @PostMapping("/free")
    public ApiResponse<List<MissionAiResDTO>> getFreeMission(@AuthenticationPrincipal Long userId, @RequestBody FreeMissionReqDTO freeMissionReqDTO){
        List<MissionAiResDTO> freeMissions = missionService.requestFreeMission(userId, freeMissionReqDTO.getSubCategory());

        return ApiResponse.success(freeMissions);
    }
}
