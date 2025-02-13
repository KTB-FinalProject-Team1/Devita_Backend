package com.devita.domain.mission.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FreeMissionAiResDTO {
    private List<MissionAiResDTO> missions;
}
