package com.devita.domain.category.controller;

import com.devita.common.response.ApiResponse;
import com.devita.domain.category.domain.Category;
import com.devita.domain.category.dto.CategoryRequestDto;
import com.devita.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/todo")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category")
    public ApiResponse<Long> createCategory(@AuthenticationPrincipal Long userId, @RequestBody CategoryRequestDto categoryRequestDto) {
        Long categoryId = categoryService.createCategory(userId, categoryRequestDto).getId();

        return ApiResponse.success(categoryId);
    }

    @PutMapping("/category/{categoryId}")
    public ApiResponse<Long> updateCategory(@AuthenticationPrincipal Long userId, @PathVariable Long categoryId, @RequestBody CategoryRequestDto categoryRequestDto) {
        Category updatedCategory = categoryService.updateCategory(userId, categoryId, categoryRequestDto);

        return ApiResponse.success(updatedCategory.getId());
    }

    @DeleteMapping("/category/{categoryId}")
    public ApiResponse<Void> deleteCategory(@AuthenticationPrincipal Long userId, @PathVariable Long categoryId) {
        categoryService.deleteCategory(userId, categoryId);

        return ApiResponse.success(null);
    }
}
