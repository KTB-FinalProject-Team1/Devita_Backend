package com.devita.domain.mission.dto;

import lombok.*;

// 미션 응답 DTO
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MissionResDTO {
    private String missionTitle;    // 미션 제목
}