package com.devita.domain.mission.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionReqDTO {
    private Long userId; // 미션을 요청하는 유저의 ID
//    private String category;    // 미션 카테고리 (예: C++, Java)
}
