package io.hexlet.spring.controller;

import io.hexlet.spring.exception.ResourceNotFoundException;
import io.hexlet.spring.model.User;
import io.hexlet.spring.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> index() {
        List<User> result = userRepository.findAll();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        userRepository.save(user);
        URI uri = URI.create("/api/users/" + user.getId());
        return ResponseEntity.created(uri).body(user);
    }

    @GetMapping("/{id}")
    public User show(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody User data) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
        user.setFirstName(data.getFirstName());
        user.setLastName(data.getLastName());
        user.setEmail(data.getEmail());
        user.setBirthday(data.getBirthday());
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
