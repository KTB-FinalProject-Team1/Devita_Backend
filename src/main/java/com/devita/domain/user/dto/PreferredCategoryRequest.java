package com.devita.domain.user.dto;

import com.devita.domain.user.domain.PreferredCategory;
import lombok.Data;

import java.util.List;

@Data
public class PreferredCategoryRequest {
    private List<PreferredCategory> categories;
}
