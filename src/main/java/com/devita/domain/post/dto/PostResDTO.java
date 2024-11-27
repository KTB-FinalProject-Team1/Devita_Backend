package com.devita.domain.post.dto;

import com.devita.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostResDTO {
    private Long id;
    private String writer;
    private String title;
    private String description;
    private Long likes;
    private Long views;
}
