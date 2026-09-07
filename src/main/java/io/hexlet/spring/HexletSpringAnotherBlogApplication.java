package io.hexlet.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HexletSpringAnotherBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(HexletSpringAnotherBlogApplication.class, args);
    }
}
