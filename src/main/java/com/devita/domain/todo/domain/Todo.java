package com.devita.domain.todo.domain;

import com.devita.domain.category.domain.Category;
import com.devita.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long todoId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String title;
    public boolean status;
    private LocalDate date;
    private String createdAt;
    private String updatedAt;

    public void toggleSatatus() {
        this.status = !this.status;
    }
}