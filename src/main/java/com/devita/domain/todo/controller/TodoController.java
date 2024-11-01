package com.devita.domain.todo.controller;

import com.devita.common.response.ApiResponse;
import com.devita.domain.todo.dto.CalenderDTO;
import com.devita.domain.todo.dto.TodoRequestDto;
import com.devita.domain.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/calendar")
    public ApiResponse<List<CalenderDTO>> getCalendar(@RequestParam String viewType) {
        List<CalenderDTO> todos = todoService.getCalendar(viewType);
        return ApiResponse.success(todos);
    }

    @PostMapping
    public ApiResponse<Long> addTodo(@AuthenticationPrincipal Long userId, @RequestBody TodoRequestDto todoRequestDto) {
        System.out.println(todoRequestDto.toString());
        Long todoId = todoService.addTodo(userId, todoRequestDto).getId();

        return ApiResponse.success(todoId);
    }

    @PutMapping("/{todoId}")
    public ApiResponse<Long> updateTodo(@AuthenticationPrincipal Long userId, @PathVariable Long todoId, @RequestBody TodoRequestDto todoRequestDto) {
        todoService.updateTodo(userId, todoId, todoRequestDto);

        return ApiResponse.success(todoId);
    }

    @DeleteMapping("/{todoId}")
    public ApiResponse<Void> deleteTodo(@AuthenticationPrincipal Long userId, @PathVariable Long todoId) {
        todoService.deleteTodo(userId, todoId);

        return ApiResponse.success(null);
    }

    @PutMapping("/{todoId}/toggle")
    public ApiResponse<Void> toggleTodoCompletion(@PathVariable Long todoId) {
        todoService.toggleTodo(todoId);

        return ApiResponse.success(null);
    }


}