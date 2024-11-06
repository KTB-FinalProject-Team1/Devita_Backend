package com.devita.domain.mission.service;

import com.devita.domain.mission.dto.ai.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final RestTemplate restTemplate;
    @Value("${ai.address}")
    private String aiAddress;
    private static final String DAILY_MISSION_API = "/ai/v1/mission/daily";
    private static final String FREE_MISSION_API = "/ai/v1/mission/free";

    public DailyMissionAiResDTO requestDailyMission(Long userId, String category) {
        // AI 서버 호출 부분 주석 처리
        // DailyMissionAiReqDTO request = new DailyMissionAiReqDTO(userId, categp);
        // return restTemplate.postForObject(aiAddress + DAILY_MISSION_API, request, MissionAiResDTO.class);

        // 테스트용 임의 데이터 반환
        DailyMissionAiResDTO testResponse = new DailyMissionAiResDTO();
        testResponse.setMissionTitle("테스트용 다형성 공부하기");
        return testResponse;
    }

    public List<MissionAiResDTO> requestFreeMission(Long userId, String subCategory) {
        // AI 서버 호출 부분 주석 처리
        // FreeMissionAiReqDTO request = new FreeMissionAiReqDTO(userId, subCategory);
        // FreeMissionAiResDTO response = restTemplate.postForObject(aiAddress + FREE_MISSION_API, request, FreeMissionAiResDTO.class);
        // return response != null ? response.getMissions() : List.of();

        // 테스트용 임의 데이터 반환
        MissionAiResDTO mission1 = new MissionAiResDTO();
        mission1.setLevel(1);
        mission1.setMissionTitle("테스트용 쉬운 미션");

        MissionAiResDTO mission2 = new MissionAiResDTO();
        mission2.setLevel(2);
        mission2.setMissionTitle("테스트용 중간 난이도 미션");

        MissionAiResDTO mission3 = new MissionAiResDTO();
        mission3.setLevel(3);
        mission3.setMissionTitle("테스트용 어려운 미션");

        return List.of(mission1, mission2, mission3);
    }
}
