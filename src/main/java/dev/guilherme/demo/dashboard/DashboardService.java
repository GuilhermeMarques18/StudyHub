package dev.guilherme.demo.dashboard;

import dev.guilherme.demo.dashboard.dto.DashboardDTO;
import dev.guilherme.demo.dashboard.dto.SubjectProgressDTO;
import dev.guilherme.demo.dashboard.dto.WeeklyTrendDTO;
import dev.guilherme.demo.study.StudySessionRepository;
import dev.guilherme.demo.subject.SubjectModel;
import dev.guilherme.demo.subject.SubjectRepository;
import dev.guilherme.demo.user.UserModel;
import dev.guilherme.demo.user.usergoal.UserGoalModel;
import dev.guilherme.demo.user.usergoal.UserGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudySessionRepository studySessionRepository;
    private final SubjectRepository subjectRepository;
    private final UserGoalRepository userGoalRepository;

    public DashboardDTO getMainDashboard() {
        UserModel user = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        LocalDateTime startOfWeek = now.toLocalDate().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);

        Integer dailyMinutes = Optional.ofNullable(
                studySessionRepository.getDailyMinutes(user.getId(), startOfDay, endOfDay)
        ).orElse(0);

        Integer weekMinutes = Optional.ofNullable(
                studySessionRepository.getWeeklyMinutes(user.getId(), startOfWeek, endOfWeek)
        ).orElse(0);

        Double dailyHours = dailyMinutes / 60.0;
        Double weekHours = weekMinutes / 60.0;

        Optional<UserGoalModel> goalOpt = userGoalRepository.findByUser(user);
        Double weekProgress = goalOpt.map(g -> g.getWeeklyGoal() > 0
                ? (weekHours / g.getWeeklyGoal()) * 100 : 0.0).orElse(0.0);
        Double dailyProgress = goalOpt.map(g -> g.getDailyGoal() > 0
                ? (dailyHours / g.getDailyGoal()) * 100 : 0.0).orElse(0.0);

        Long totalSessions = studySessionRepository.countByUserId(user.getId());
        Double totalHours = Optional.ofNullable(
                studySessionRepository.getTotalHours(user.getId())
        ).orElse(0.0);

        Integer streak = calculateStreak(user.getId());
        List<SubjectProgressDTO> subjects = getSubjectProgress(user);
        List<WeeklyTrendDTO> weeklyTrend = getWeeklyTrend(user.getId());

        return new DashboardDTO(
                weekProgress,
                dailyProgress,
                totalSessions,
                totalHours,
                streak,
                getStatus(weekProgress, dailyProgress),
                subjects,
                weeklyTrend
        );
    }

    private List<SubjectProgressDTO> getSubjectProgress(UserModel user) {
        List<SubjectModel> subjects = subjectRepository.findByUserIdOrderByName(user.getId());
        if (subjects.isEmpty()) return List.of();

        List<Long> ids = subjects.stream().map(SubjectModel::getId).toList();
        Map<Long, Double> minutesById = studySessionRepository
                .sumDurationBySubjectIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Number) row[1]).doubleValue()
                ));

        return subjects.stream().map(subject -> {
            double hours = minutesById.getOrDefault(subject.getId(), 0.0) / 60.0;
            double progress = subject.getGoal() != null && subject.getGoal() > 0
                    ? (hours / subject.getGoal()) * 100
                    : 0.0;
            return new SubjectProgressDTO(
                    subject.getId(),
                    subject.getName(),
                    subject.getColor(),
                    hours,
                    subject.getGoal(),
                    Math.min(progress, 100.0)
            );
        }).toList();
    }

    private List<WeeklyTrendDTO> getWeeklyTrend(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        List<WeeklyTrendDTO> trend = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);
            LocalDateTime start = day.atStartOfDay();
            LocalDateTime end = start.plusDays(1);

            Integer minutes = Optional.ofNullable(
                    studySessionRepository.getDailyMinutes(userId, start, end)
            ).orElse(0);

            String dayName = day.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));

            trend.add(new WeeklyTrendDTO(dayName, minutes / 60.0));
        }
        return trend;
    }

    private Integer calculateStreak(Long userId) {
        LocalDateTime limitDate = LocalDateTime.now().minusDays(365);
        List<java.sql.Date> studyDates = studySessionRepository.findStudyDatesByUser(userId, limitDate);

        if (studyDates.isEmpty()) return 0;

        LocalDate today = LocalDate.now();
        int streak = 0;

        LocalDate lastStudyDate = studyDates.get(0).toLocalDate();

        if (!lastStudyDate.isEqual(today) && !lastStudyDate.isEqual(today.minusDays(1))) {
            return 0;
        }

        LocalDate currentDateCheck = lastStudyDate;

        for (java.sql.Date sqlDate : studyDates) {
            LocalDate date = sqlDate.toLocalDate();

            if (date.isEqual(currentDateCheck)) {
                streak++;
                currentDateCheck = currentDateCheck.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }

    private String getStatus(Double weekProgress, Double dailyProgress) {
        if (weekProgress >= 90 && dailyProgress >= 90) return "🟢 Excelente!";
        if (weekProgress >= 70) return "🟡 No caminho!";
        return "🔴 Vamos estudar!";
    }

    private UserModel getCurrentUser() {
        return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}