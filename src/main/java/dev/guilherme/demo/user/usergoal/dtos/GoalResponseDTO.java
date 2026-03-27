package dev.guilherme.demo.user.usergoal.dtos;

public record GoalResponseDTO(Long id,
                              Double weeklyGoal,
                              Double dailyGoal,
                              Double weekProgress,
                              Double dailyProgress,
                              String status) {}
