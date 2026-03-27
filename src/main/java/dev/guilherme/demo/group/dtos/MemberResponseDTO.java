package dev.guilherme.demo.group.dtos;

import java.time.LocalDateTime;

public record MemberResponseDTO(
        Long id,
        String name,
        String email,
        String role,
        LocalDateTime joinedDate
) {}
