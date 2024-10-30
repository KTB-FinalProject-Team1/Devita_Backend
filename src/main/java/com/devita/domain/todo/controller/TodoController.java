package com.devita.domain.todo.controller;

import com.devita.domain.todo.domain.Category;
import com.devita.domain.todo.domain.Todo;
import com.devita.domain.todo.dto.CalenderDTO;
import com.devita.domain.todo.dto.CategoryRequestDto;
import com.devita.domain.todo.dto.TodoRequestDto;
import com.devita.domain.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/calendar")
    public ResponseEntity<List<CalenderDTO>> getCalendar(@RequestParam String viewType) {
        // 캘린더 조회 로직을 서비스에 구현할 필요가 있습니다.
        List<CalenderDTO> todos = todoService.getCalendar(viewType);
        return ResponseEntity.ok(todos);
    }

    @PostMapping()
    public ResponseEntity<Long> addTodo(@AuthenticationPrincipal Long userId, @RequestBody TodoRequestDto todoRequestDto) {
        Long todoId = todoService.addTodo(userId, todoRequestDto).getTodoId();

        return ResponseEntity.status(HttpStatus.CREATED).body(todoId);
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<Long> updateTodo(@AuthenticationPrincipal Long userId, @PathVariable Long todoId, @RequestBody TodoRequestDto todoRequestDto) {
        Todo todo = todoService.updateTodo(userId, todoId, todoRequestDto);

        return ResponseEntity.ok(todoId);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteTodo(@AuthenticationPrincipal Long userId,  @PathVariable Long todoId) {
        todoService.deleteTodo(userId, todoId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{todoId}/toggle")
    public ResponseEntity<Void> toggleTodoCompletion(@PathVariable Long todoId) {
        todoService.toggleTodo(todoId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/category")
    public ResponseEntity<Long> createCategory(@AuthenticationPrincipal Long userId, @RequestBody CategoryRequestDto categoryRequestDto) {
        Long categoryId = todoService.createCategory(userId, categoryRequestDto).getCategoryId();

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryId);
    }

    @PutMapping("/category/{categoryId}")
    public ResponseEntity<Long> updateCategory(@AuthenticationPrincipal Long userId, @PathVariable Long categoryId, @RequestBody CategoryRequestDto categoryRequestDto) {
        Category updatedCategory = todoService.updateCategory(userId, categoryId, categoryRequestDto);

        return ResponseEntity.ok(updatedCategory.getCategoryId());
    }

    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal Long userId,  @PathVariable Long categoryId) {
        todoService.deleteCategory(userId, categoryId);

        return ResponseEntity.noContent().build();
    }
}