package com.devita.domain.user.service;

import com.devita.common.exception.ErrorCode;
import com.devita.common.exception.ResourceNotFoundException;
import com.devita.domain.user.domain.User;
import com.devita.domain.user.dto.PreferredCategoryRequest;
import com.devita.domain.user.dto.PreferredCategoryResponse;
import com.devita.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void updatePreferredCategories(Long userId, PreferredCategoryRequest request) {
        User user = getUserById(userId);
        user.updatePreferredCategories(request.getCategories());
    }

    public PreferredCategoryResponse getPreferredCategories(Long userId) {
        User user = getUserById(userId);
        return new PreferredCategoryResponse(user.getPreferredCategories());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
    }
}