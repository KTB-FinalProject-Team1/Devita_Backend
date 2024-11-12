package com.devita.domain.mission.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
public class DailyMissionResDTO {
    private Long missionId;
    private String missionTitle;
}
