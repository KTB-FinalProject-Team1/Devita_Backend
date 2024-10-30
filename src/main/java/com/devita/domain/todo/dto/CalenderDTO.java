package com.devita.domain.todo.dto;

import com.devita.domain.todo.domain.Todo;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CalenderDTO {
    private Long todoId;
    private Long categoryId;
    private String title;
    private String status;
    private LocalDate date;


    public static CalenderDTO fromEntity(Todo todo) {
        CalenderDTO dto = new CalenderDTO();
        dto.setTodoId(todo.getTodoId());
        dto.setCategoryId(todo.getCategory().getCategoryId());
        dto.setTitle(todo.getTitle());
        dto.setDate(todo.getDate());

        return dto;
    }
}