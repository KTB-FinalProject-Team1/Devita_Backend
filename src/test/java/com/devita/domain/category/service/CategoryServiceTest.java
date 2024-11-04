package com.devita.domain.category.service;

import com.devita.common.exception.AccessDeniedException;
import com.devita.common.exception.ResourceNotFoundException;
import com.devita.domain.category.domain.Category;
import com.devita.domain.category.dto.CategoryReqDTO;
import com.devita.domain.category.repository.CategoryRepository;
import com.devita.domain.user.domain.User;
import com.devita.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User testUser;
    private Category testCategory;
    private CategoryReqDTO testCategoryReqDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setUser(testUser);
        testCategory.setName("Test Category");

        testCategoryReqDTO = new CategoryReqDTO();
        testCategoryReqDTO.setName("Test Category");
    }

    @Test
    void createCategory_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.createCategory(1L, testCategoryReqDTO);

        assertNotNull(result);
        assertEquals(testCategory.getName(), result.getName());
        assertEquals(testUser, result.getUser());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));

        assertDoesNotThrow(() -> categoryService.deleteCategory(1L, 1L));
        verify(categoryRepository).delete(testCategory);
    }

    @Test
    void deleteCategory_AccessDenied() {
        testCategory.getUser().setId(2L); // Different user
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));

        assertThrows(AccessDeniedException.class, () -> categoryService.deleteCategory(1L, 1L));
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void updateCategory_Success() {
        CategoryReqDTO updateDto = new CategoryReqDTO();
        updateDto.setName("Updated Category");

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.updateCategory(1L, 1L, updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getName(), result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_NotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(1L, 1L, testCategoryReqDTO));
    }

    @Test
    void updateCategory_AccessDenied() {
        testCategory.getUser().setId(2L); // Different user
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));

        assertThrows(AccessDeniedException.class,
                () -> categoryService.updateCategory(1L, 1L, testCategoryReqDTO));
    }
}