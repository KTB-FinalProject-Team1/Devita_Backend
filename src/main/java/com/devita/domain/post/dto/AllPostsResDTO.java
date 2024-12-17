package com.devita.domain.post.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AllPostsResDTO(
        Long id,
        String title,
        String description,
        Long writerId,
        String writerNickname,
        Long likes,
        Long views,
        LocalDateTime createdAt,
        boolean isLiked
) {}
