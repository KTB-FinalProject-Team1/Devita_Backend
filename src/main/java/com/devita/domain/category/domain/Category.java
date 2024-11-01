package com.devita.domain.category.domain;

import com.devita.domain.todo.domain.Todo;
import com.devita.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter //테스트 코드용
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;
    @CreatedDate
    private String createdAt;
    @LastModifiedDate
    private String updatedAt;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos;

    public Category(User user, String name){
        this.user = user;
        this.name = name;
        todos = new ArrayList<>();
    }

}