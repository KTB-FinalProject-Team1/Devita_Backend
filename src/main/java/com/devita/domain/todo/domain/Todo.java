package com.devita.domain.todo.domain;

import com.devita.domain.category.domain.Category;
import com.devita.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String title;
    private Boolean status;
    private LocalDate date;
    private String createdAt;
    private String updatedAt;

    public Todo(User user, Category category, String title, LocalDate date) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.status = false;
        this.date = date;
    }

    public void toggleSatatus() {
        this.status = !this.status;
    }
}