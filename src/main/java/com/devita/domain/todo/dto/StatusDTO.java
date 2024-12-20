package com.devita.domain.todo.dto;

public record StatusDTO(
        Long todoId,
        Long categoryId,
        String title,
        Boolean status
) {}
