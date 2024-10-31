package com.devita.domain.todo.service;

import com.devita.common.exception.AccessDeniedException;
import com.devita.common.exception.ErrorCode;
import com.devita.common.exception.ResourceNotFoundException;
import com.devita.domain.category.domain.Category;
import com.devita.domain.todo.domain.Todo;
import com.devita.domain.todo.dto.CalenderDTO;
import com.devita.domain.category.dto.CategoryRequestDto;
import com.devita.domain.todo.dto.TodoRequestDto;
import com.devita.domain.category.repository.CategoryRepository;
import com.devita.domain.todo.repository.TodoRepository;
import com.devita.domain.user.domain.User;
import com.devita.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 할 일 추가
    public Todo addTodo(Long userId, TodoRequestDto todoRequestDto) {
        User user = userRepository.findById(userId).orElseThrow();
        Category category = categoryRepository.findById(todoRequestDto.getCategoryId()).orElseThrow();

        Todo todo = new Todo();
        todo.setUser(user);
        todo.setCategory(category);
        todo.setTitle(todoRequestDto.getTitle());
        todo.setStatus(false);
        todo.setDate(todoRequestDto.getDate());

        return todoRepository.save(todo);
    }

    public void deleteTodo(Long userId, Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .filter(t -> t.getUser().getId().equals(userId))  // userId가 일치하는지 확인
                .orElseThrow(() -> new AccessDeniedException(ErrorCode.TODO_ACCESS_DENIED));

        todoRepository.delete(todo);
    }

    public Todo updateTodo(Long userId, Long todoId, TodoRequestDto todoRequestDto) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TODO_NOT_FOUND));  // 투두가 없으면 ResourceNotFoundException 발생

        if (!todo.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(ErrorCode.TODO_ACCESS_DENIED);  // userId가 일치하지 않으면 AccessDeniedException 발생
        }

        todo.setCategory(categoryRepository.findById(todoRequestDto.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND)));
        todo.setTitle(todoRequestDto.getTitle());
        todo.setDate(todoRequestDto.getDate());

        return todoRepository.save(todo);
    }

    // 할 일 완료 토글
    public void toggleTodo(Long todoId) {
        Todo todo = todoRepository.findById(todoId).orElseThrow();
        todo.toggleSatatus();
        todoRepository.save(todo);
    }

    public List<CalenderDTO> getCalendar(String viewType) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        // viewType에 따라 조회 기간 설정
        switch (viewType.toLowerCase()) {
            case "weekly":
                startDate = today.with(java.time.DayOfWeek.MONDAY);
                endDate = today.with(java.time.DayOfWeek.SUNDAY);
                break;

            case "monthly":
                startDate = today.with(TemporalAdjusters.firstDayOfMonth()); // 월의 시작일
                endDate = today.with(TemporalAdjusters.lastDayOfMonth());    // 월의 끝일
                break;

            default:
                throw new IllegalArgumentException("Invalid viewType. Use 'daily', 'weekly', or 'monthly'.");
        }

        List<Todo> todos = todoRepository.findByDateBetween(startDate, endDate);

        return todos.stream().map(CalenderDTO::fromEntity).toList();
    }
}