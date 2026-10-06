package io.hexlet.spring.controller;

import io.hexlet.spring.dto.UserCreateDTO;
import io.hexlet.spring.dto.UserDTO;
import io.hexlet.spring.dto.UserUpdateDTO;
import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.mapper.UserMapper;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> index() {
        List<User> result = userRepository.findAll();
        return ResponseEntity.ok(result.stream().map(userMapper::toDTO).toList());
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        User user = userMapper.toEntity(userCreateDTO);
        User saved = userRepository.save(user);
        URI uri = URI.create("/api/users/" + saved.getId());
        return ResponseEntity.created(uri).body(userMapper.toDTO(saved));
    }

    @GetMapping("/{id}")
    public UserDTO show(@PathVariable Long id) {
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
        return userMapper.toDTO(user);
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
        userMapper.updateEntity(user, userUpdateDTO);
        return userMapper.toDTO(userRepository.save(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
