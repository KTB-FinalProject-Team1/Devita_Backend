package com.devita.domain.post.domain;

import com.devita.common.entity.BaseEntity;
import com.devita.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Long likes;
    private Long views;

    @Builder
    private Post(User writer, String title, String description, Long likes, Long views) {
        this.writer = writer;
        this.title = title;
        this.description = description;
        this.likes = likes;
        this.views = views;
    }

    public void updatePost(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void increaseView() {
        views += 1;
    }
}