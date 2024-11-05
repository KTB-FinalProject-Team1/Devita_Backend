package com.devita.domain.mission.controller;


import com.devita.common.response.ApiResponse;
import com.devita.domain.mission.dto.ai.MissionAiResDTO;
import com.devita.domain.mission.dto.client.FreeMissionReqDTO;
import com.devita.domain.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mission")
public class MissionController {
    private final MissionService missionService;

    @GetMapping("/free")
    public ApiResponse<List<MissionAiResDTO>> getFreeMission(@AuthenticationPrincipal Long userId, FreeMissionReqDTO freeMissionReqDTO){
        List<MissionAiResDTO> freeMissions = missionService.requestFreeMission(userId, freeMissionReqDTO.getSubCategory());

        return ApiResponse.success(freeMissions);
    }
}
