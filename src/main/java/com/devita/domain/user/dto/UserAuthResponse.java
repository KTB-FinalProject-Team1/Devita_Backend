package com.devita.domain.user.dto;

import com.devita.domain.category.dto.CategoryResDTO;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Data
public class UserAuthResponse {
    private String accessToken;
    private String email;
    private String nickname;
    private List<CategoryResDTO> categories;
}