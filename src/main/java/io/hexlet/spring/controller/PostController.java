package io.hexlet.spring.controller;

import io.hexlet.spring.dto.PostCreateDTO;
import io.hexlet.spring.dto.PostDTO;
import io.hexlet.spring.dto.PostUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.PostMapper;
import io.hexlet.spring.model.Post;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.PostRepository;
import io.hexlet.spring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;

    public PostController(PostRepository postRepository, PostMapper postMapper, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.userRepository = userRepository;
    }

    @GetMapping
    public Page<PostDTO> getPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] parts = sort.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        return postRepository.findByPublishedTrue(pageable).map(postMapper::toDTO);
    }

    @PostMapping
    public ResponseEntity<PostDTO> create(@Valid @RequestBody PostCreateDTO postCreateDTO) {
        User user = userRepository.findById(postCreateDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User " + postCreateDTO.getUserId() + " not found"));
        Post post = postMapper.toEntity(postCreateDTO, user);
        PostDTO postDTO = postMapper.toDTO(postRepository.save(post));
        return ResponseEntity.status(HttpStatus.CREATED).body(postDTO);
    }

    @GetMapping("/{id}")
    public PostDTO show(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post " + id + " not found"));
        return postMapper.toDTO(post);
    }

    @PutMapping("/{id}")
    public PostDTO update(@PathVariable Long id, @Valid @RequestBody PostUpdateDTO postUpdateDTO) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post " + id + " not found"));
        postMapper.updateEntity(post, postUpdateDTO);
        return postMapper.toDTO(postRepository.save(post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post " + id + " not found"));
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
