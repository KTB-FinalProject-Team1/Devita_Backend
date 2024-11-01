package com.devita.domain.category.controller;

import com.devita.common.response.ApiResponse;
import com.devita.domain.category.domain.Category;
import com.devita.domain.category.dto.CategoryReqDto;
import com.devita.domain.category.dto.CategoryResDto;
import com.devita.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todo")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category")
    public ApiResponse<Long> createCategory(@AuthenticationPrincipal Long userId, @RequestBody CategoryReqDto categoryReqDto) {
        Long categoryId = categoryService.createCategory(userId, categoryReqDto).getId();

        return ApiResponse.success(categoryId);
    }

    @PutMapping("/category/{categoryId}")
    public ApiResponse<Long> updateCategory(@AuthenticationPrincipal Long userId, @PathVariable Long categoryId, @RequestBody CategoryReqDto categoryReqDto) {
        Category updatedCategory = categoryService.updateCategory(userId, categoryId, categoryReqDto);

        return ApiResponse.success(updatedCategory.getId());
    }

    @DeleteMapping("/category/{categoryId}")
    public ApiResponse<Void> deleteCategory(@AuthenticationPrincipal Long userId, @PathVariable Long categoryId) {
        categoryService.deleteCategory(userId, categoryId);

        return ApiResponse.success(null);
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResDto>> findUserCategories(@AuthenticationPrincipal Long userId){
        List<CategoryResDto> categories = categoryService.findUserCategories(userId);

        return ApiResponse.success(categories);
    }
}
