package com.devita.domain.category.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResDTO {
    private Long id;
    private String name;
    private String color;
}
