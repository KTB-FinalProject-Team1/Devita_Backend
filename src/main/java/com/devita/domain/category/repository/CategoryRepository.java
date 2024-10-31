package com.devita.domain.category.repository;

import com.devita.domain.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    void deleteByCategoryId(Long id);
}
