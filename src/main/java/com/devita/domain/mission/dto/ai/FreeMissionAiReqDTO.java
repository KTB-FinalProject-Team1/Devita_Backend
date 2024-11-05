package com.devita.domain.mission.dto.ai;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FreeMissionAiReqDTO {
    private Long userId;
    private String subCategory;
}
