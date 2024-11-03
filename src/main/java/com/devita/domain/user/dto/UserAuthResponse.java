package com.devita.domain.user.dto;

import com.devita.domain.category.dto.CategoryResDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserAuthResponse {
    private String accessToken;
    private String email;
    private String nickname;
    private List<CategoryResDto> categories;
}