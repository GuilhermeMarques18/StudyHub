package dev.guilherme.demo.group.dtos;

public record GroupResponseDTO(
        Long id,
        String name,
        String description,
        Boolean isPrivate,
        Long memberCount,
        String creatorName
) {}
