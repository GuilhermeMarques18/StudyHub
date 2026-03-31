package dev.guilherme.demo.dashboard.dto;

import java.util.List;

public record DashboardDTO(Double weekProgress,
                           Double dailyProgress,
                           Long totalSessions,
                           Double totalHours,
                           Integer streak,
                           String status,
                           List<SubjectProgressDTO> subjects,
                           List<WeeklyTrendDTO> weeklyTrend) {
}
