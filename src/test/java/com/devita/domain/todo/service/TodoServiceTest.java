package com.devita.domain.todo.service;

import com.devita.common.exception.AccessDeniedException;
import com.devita.domain.category.domain.Category;
import com.devita.domain.category.repository.CategoryRepository;
import com.devita.domain.todo.domain.Todo;
import com.devita.domain.todo.dto.CalenderDTO;
import com.devita.domain.todo.dto.TodoRequestDto;
import com.devita.domain.todo.repository.TodoRepository;
import com.devita.domain.user.domain.User;
import com.devita.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoService todoService;

    private User testUser;
    private Category testCategory;
    private Todo testTodo;
    private TodoRequestDto testTodoRequestDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setUser(testUser);

        testTodo = new Todo();
        testTodo.setId(1L);
        testTodo.setUser(testUser);
        testTodo.setCategory(testCategory);
        testTodo.setTitle("Test Todo");
        testTodo.setStatus(false);
        testTodo.setDate(LocalDate.now());

        testTodoRequestDto = new TodoRequestDto();
        testTodoRequestDto.setCategoryId(1L);
        testTodoRequestDto.setTitle("Test Todo");
        testTodoRequestDto.setDate(LocalDate.now());
    }

    @Test
    void addTodo_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        Todo result = todoService.addTodo(1L, testTodoRequestDto);

        assertNotNull(result);
        assertEquals(testTodo.getTitle(), result.getTitle());
        assertEquals(testUser, result.getUser());
        assertEquals(testCategory, result.getCategory());
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void deleteTodo_Success() {
        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(testTodo));

        assertDoesNotThrow(() -> todoService.deleteTodo(1L, 1L));
        verify(todoRepository).delete(testTodo);
    }

    @Test
    void deleteTodo_AccessDenied() {
        testTodo.getUser().setId(2L); // Different user
        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(testTodo));

        assertThrows(AccessDeniedException.class, () -> todoService.deleteTodo(1L, 1L));
        verify(todoRepository, never()).delete(any());
    }

    @Test
    void updateTodo_Success() {
        TodoRequestDto updateDto = new TodoRequestDto();
        updateDto.setCategoryId(1L);
        updateDto.setTitle("Updated Todo");
        updateDto.setDate(LocalDate.now());

        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(testTodo));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        Todo result = todoService.updateTodo(1L, 1L, updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getTitle(), result.getTitle());
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void toggleTodo_Success() {
        boolean initialStatus = testTodo.getStatus();
        when(todoRepository.findById(anyLong())).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        todoService.toggleTodo(1L);

        assertNotEquals(initialStatus, testTodo.getStatus());
        verify(todoRepository).save(testTodo);
    }

    @Test
    void getCalendar_Weekly_Success() {
        List<Todo> todoList = Arrays.asList(testTodo);

        when(todoRepository.findByUserIdAndDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(todoList);

        List<CalenderDTO> result = todoService.getCalendar(1L, "weekly");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(todoRepository).findByUserIdAndDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getCalendar_InvalidViewType() {
        assertThrows(IllegalArgumentException.class, () -> todoService.getCalendar(1L, "invalid"));
    }
}