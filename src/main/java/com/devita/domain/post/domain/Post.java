package com.devita.domain.post.domain;

import com.devita.common.entity.BaseEntity;
import com.devita.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Post extends BaseEntity {
    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "bigint default 0")
    private Long views = 0L;

    @Builder
    private Post(User writer, String title, String description) {
        this.writer = writer;
        this.title = title;
        this.description = description;
        this.images = new ArrayList<>();
    }

    public void updatePost(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void increaseView() {
        views += 1;
    }
}