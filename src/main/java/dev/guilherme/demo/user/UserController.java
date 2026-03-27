package dev.guilherme.demo.user;


import dev.guilherme.demo.user.dtos.UserDTO;
import dev.guilherme.demo.user.dtos.UserResponseDTO;
import dev.guilherme.demo.user.usergoal.dtos.GoalResponseDTO;
import dev.guilherme.demo.user.usergoal.dtos.GoalUpdateDTO;
import dev.guilherme.demo.user.usergoal.UserGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import dev.guilherme.demo.user.exception.UserAlreadyExistsException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserGoalService userGoalService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> saveUser(@Valid @RequestBody UserDTO dto) {
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


    @PutMapping("/goals")
    public ResponseEntity<GoalResponseDTO> updateGoal(@RequestBody GoalUpdateDTO dto) {
        return ResponseEntity.ok(userGoalService.updateGoal(dto));
    }

    @GetMapping("/goals")
    public ResponseEntity<GoalResponseDTO> getGoal() {
        return ResponseEntity.ok(userGoalService.getCurrentGoal());
    }

    private UserResponseDTO toDto(UserModel user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getCreatedDate());
    }
}
