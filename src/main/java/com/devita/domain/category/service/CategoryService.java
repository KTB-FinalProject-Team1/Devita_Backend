package com.devita.domain.category.service;

import com.devita.common.exception.AccessDeniedException;
import com.devita.common.exception.ErrorCode;
import com.devita.common.exception.ResourceNotFoundException;
import com.devita.domain.category.domain.Category;
import com.devita.domain.category.dto.CategoryReqDTO;
import com.devita.domain.category.dto.CategoryResDTO;
import com.devita.domain.category.repository.CategoryRepository;
import com.devita.domain.user.domain.User;
import com.devita.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // 카테고리 추가
    public Category createCategory(Long userId, CategoryReqDTO categoryReqDto) {
        User user = userRepository.findById(userId).orElseThrow();

        Category category = new Category();
        category.setUser(user);
        category.setName(categoryReqDto.getName());

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .filter(t -> t.getUser().getId().equals(userId))  // userId가 일치하는지 확인
                .orElseThrow(() -> new AccessDeniedException(ErrorCode.CATEGORY_ACCESS_DENIED));

        categoryRepository.delete(category);
    }

    public Category updateCategory(Long userId, Long categoryId, CategoryReqDTO categoryReqDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!category.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(ErrorCode.CATEGORY_ACCESS_DENIED);  // userId가 일치하지 않으면 AccessDeniedException 발생
        }

        category.setName(categoryReqDto.getName());

        return categoryRepository.save(category);
    }

    public List<CategoryResDTO> findUserCategories(Long userId) {
        List<Category> categories = categoryRepository.findByUserId(userId);
        List<CategoryResDTO> categoryResDTOS = new ArrayList<>();

        for (Category category: categories){
            CategoryResDTO categoryResDto = new CategoryResDTO();

            categoryResDto.setId(category.getId());
            categoryResDto.setName(category.getName());

            categoryResDTOS.add(categoryResDto);
        }

        return categoryResDTOS;
    }
}
