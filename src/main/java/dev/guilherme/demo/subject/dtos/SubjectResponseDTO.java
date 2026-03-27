package dev.guilherme.demo.subject.dtos;

public record SubjectResponseDTO(
        Long id,
        String name,
        String description,
        Double targetHours,
        String color,
        Double hoursStudied,
        Double progressPercentage
) {}