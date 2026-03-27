package dev.guilherme.demo.study.dtos;

import dev.guilherme.demo.study.DifficultyLevel;

import java.time.LocalDateTime;

public record  StudySessionResponseDTO(Long id,
                                       LocalDateTime startTime,
                                       LocalDateTime endTime,
                                       Integer durationMinutes,
                                       DifficultyLevel difficultyLevel,
                                       String notes,
                                       Long topicId,
                                       Long subjectId,
                                       String subjectName,
                                       LocalDateTime createdDate) {
}
