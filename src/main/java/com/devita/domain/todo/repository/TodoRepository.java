package com.devita.domain.todo.repository;

import com.devita.domain.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

}