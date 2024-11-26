package com.devita.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostsResDTO {
    private Long id;
    private String title;
    private String description;
    private Long likes;
    private Long views;
}
