package dev.guilherme.demo.topic.dtos;

import dev.guilherme.demo.study.DifficultyLevel;

public record TopicResponseDTO(Long id,
                               String name,
                               String description,
                               Double estimatedHours,
                               DifficultyLevel difficultyLevel,
                               Boolean completed,
                               Long subjectId) {
}
