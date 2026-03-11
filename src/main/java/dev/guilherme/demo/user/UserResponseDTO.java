package dev.guilherme.demo.user;

import java.time.LocalDateTime;

public record UserResponseDTO(Long id, String name, String email, LocalDateTime createdDate) {
}
