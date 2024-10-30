package com.devita.domain.todo.repository;

import com.devita.domain.todo.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    void deleteByCategoryId(Long id);
}
