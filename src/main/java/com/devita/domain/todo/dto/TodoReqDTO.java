package com.devita.domain.todo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TodoReqDTO {
    private Long categoryId;
    private String title;
    private LocalDate date;
}