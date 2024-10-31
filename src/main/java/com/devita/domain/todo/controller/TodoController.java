package com.devita.domain.todo.controller;

import com.devita.common.response.ApiResponse;
import com.devita.domain.todo.domain.Category;
import com.devita.domain.todo.dto.CalenderDTO;
import com.devita.domain.todo.dto.CategoryRequestDto;
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

    @PostMapping()
    public ApiResponse<Long> addTodo(@AuthenticationPrincipal Long userId, @RequestBody TodoRequestDto todoRequestDto) {
        Long todoId = todoService.addTodo(userId, todoRequestDto).getTodoId();

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

    @PostMapping("/category")
    public ApiResponse<Long> createCategory(@AuthenticationPrincipal Long userId, @RequestBody CategoryRequestDto categoryRequestDto) {
        Long categoryId = todoService.createCategory(userId, categoryRequestDto).getCategoryId();

        return ApiResponse.success(categoryId);
    }

    @PutMapping("/category/{categoryId}")
    public ApiResponse<Long> updateCategory(@AuthenticationPrincipal Long userId, @PathVariable Long categoryId, @RequestBody CategoryRequestDto categoryRequestDto) {
        Category updatedCategory = todoService.updateCategory(userId, categoryId, categoryRequestDto);

        return ApiResponse.success(updatedCategory.getCategoryId());
    }

    @DeleteMapping("/category/{categoryId}")
    public ApiResponse<Void> deleteCategory(@AuthenticationPrincipal Long userId, @PathVariable Long categoryId) {
        todoService.deleteCategory(userId, categoryId);

        return ApiResponse.success(null);
    }
}