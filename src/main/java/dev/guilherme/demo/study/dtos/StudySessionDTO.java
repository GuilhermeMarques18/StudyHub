package dev.guilherme.demo.study.dtos;

import dev.guilherme.demo.study.DifficultyLevel;
import java.time.LocalDateTime;

public record StudySessionDTO(
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer durationMinutes,
        DifficultyLevel difficultyLevel,
        String notes,
        Long topicId,
        Long subjectId
) {}