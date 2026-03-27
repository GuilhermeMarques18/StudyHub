package dev.guilherme.demo.topic.dtos;

import dev.guilherme.demo.study.DifficultyLevel;

public record TopicDTO(String name, String description, Double estimatedHours, DifficultyLevel difficultyLevel, Long subject) {
}
