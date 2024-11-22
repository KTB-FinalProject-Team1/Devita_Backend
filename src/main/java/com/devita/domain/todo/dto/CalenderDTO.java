package com.devita.domain.todo.dto;

import com.devita.domain.todo.domain.Todo;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CalenderDTO {

    private Long todoId;
    private Long categoryId;
    private String title;
    private Boolean status;
    private LocalDate date;

    public static CalenderDTO fromEntity(Todo todo) {
        CalenderDTO dto = new CalenderDTO();
        dto.setTodoId(todo.getId());
        dto.setCategoryId(todo.getCategory().getId());
        dto.setTitle(todo.getTitle());
        dto.setStatus(todo.getStatus());
        dto.setDate(todo.getDate());

        return dto;
    }
}