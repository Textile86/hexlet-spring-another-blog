package io.hexlet.spring.controller;

import io.hexlet.spring.model.Post;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class Posts {
    private List<Post> posts = new ArrayList<>();

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> index(@RequestParam(defaultValue = "10") Integer limit) {
        List<Post> result = posts.stream().limit(limit).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> create(@Valid @RequestBody Post post) {
        posts.add(post);
        URI uri = URI.create("/posts" + post.getId());
        return ResponseEntity.created(uri).body(post);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> show(@PathVariable Long id) {
        Optional<Post> post = posts.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        return ResponseEntity.of(post);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> update(@PathVariable Long id, @RequestBody Post data) {
        var maybePost = posts.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
        if (maybePost.isPresent()) {
            var post = maybePost.get();
            post.setTitle(data.getTitle());
            post.setContent(data.getContent());
            post.setAuthor(data.getAuthor());
            return ResponseEntity.ok(post);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        boolean removed = posts.removeIf(p -> p.getId().equals(id));
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
