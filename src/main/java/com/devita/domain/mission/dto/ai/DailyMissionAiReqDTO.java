package com.devita.domain.mission.dto.ai;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class DailyMissionAiReqDTO {
    private Long userId; // 미션을 요청하는 유저의 ID
    private List<String> categies;    // 미션 카테고리 (예: C++, Java)
}
