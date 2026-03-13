package dev.guilherme.demo.user.dtos;

import java.time.LocalDateTime;

public record UserResponseDTO(Long id, String name, String email, LocalDateTime createdDate) {
}
