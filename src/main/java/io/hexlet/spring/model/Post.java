package io.hexlet.spring.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Post {
    private Long id;

    @NotNull(message = "Title must not be Null")
    private String title;

    @NotNull(message = "Content must not be Null")
    private String content;
    private String author;
    private LocalDateTime createdAt;
}
