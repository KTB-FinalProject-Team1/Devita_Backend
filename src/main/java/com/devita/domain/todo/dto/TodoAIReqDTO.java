package com.devita.domain.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TodoAIReqDTO {
    String title; //Java 공부하기
    LocalDate Date; // 2024-12-02
    String type; // 일일 미션, 자율 미션
    String missionCategory; // Java, Python
    Long userId; // 1
}