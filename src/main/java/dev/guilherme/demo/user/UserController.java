package dev.guilherme.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import dev.guilherme.demo.user.exception.UserAlreadyExistsException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserDTO dto) {
        if (userService.existsByEmail(dto.email())) {
            throw new UserAlreadyExistsException(dto.email());
        }

        UserModel saved = userService.saveUser(dto);
        return ResponseEntity.ok(toDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listAll() {
        List<UserResponseDTO> users = userService.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        UserModel user = userService.findById(id);
        return ResponseEntity.ok(toDto(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        UserModel updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private UserResponseDTO toDto(UserModel user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getCreatedDate());
    }
}
