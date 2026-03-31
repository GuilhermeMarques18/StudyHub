package dev.guilherme.demo.user.usergoal;

import dev.guilherme.demo.study.StudySessionRepository;
import dev.guilherme.demo.user.UserModel;
import dev.guilherme.demo.user.usergoal.dtos.GoalResponseDTO;
import dev.guilherme.demo.user.usergoal.dtos.GoalUpdateDTO;
import dev.guilherme.demo.user.usergoal.exception.GoalNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserGoalService {

    private final UserGoalRepository userGoalRepository;
    private final StudySessionRepository studySessionRepository;

    public GoalResponseDTO getCurrentGoal() {
        UserModel user = getCurrentUser();
        UserGoalModel goal = getOrCreateGoal(user);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        LocalDateTime startOfWeek = now.toLocalDate()
                .with(DayOfWeek.MONDAY)
                .atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);

        Integer dailyMinutes = Optional.ofNullable(
                studySessionRepository.getDailyMinutes(user.getId(), startOfDay, endOfDay)
        ).orElse(0);

        Integer weekMinutes = Optional.ofNullable(
                studySessionRepository.getWeeklyMinutes(user.getId(), startOfWeek, endOfWeek)
        ).orElse(0);

        Double dailyHours = dailyMinutes / 60.0;
        Double weekHours = weekMinutes / 60.0;

        Double weekProgress = goal.getWeeklyGoal() > 0
                ? (weekHours / goal.getWeeklyGoal()) * 100
                : 0.0;
        Double dailyProgress = goal.getDailyGoal() > 0
                ? (dailyHours / goal.getDailyGoal()) * 100
                : 0.0;

        return new GoalResponseDTO(
                goal.getId(),
                goal.getWeeklyGoal(),
                goal.getDailyGoal(),
                weekProgress,
                dailyProgress,
                getStatus(weekProgress, dailyProgress)
        );
    }

    public GoalResponseDTO updateGoal(GoalUpdateDTO dto) {
        UserModel user = getCurrentUser();
        UserGoalModel goal = getOrCreateGoal(user);

        goal.setWeeklyGoal(dto.weeklyGoal());
        goal.setDailyGoal(dto.dailyGoal());
        userGoalRepository.save(goal);

        return getCurrentGoal();
    }

    public void deleteGoal() {
        UserModel user = getCurrentUser();
        UserGoalModel goal = userGoalRepository.findByUser(user)
                .orElseThrow(GoalNotFoundException::new);
        userGoalRepository.delete(goal);
    }

    private UserGoalModel getOrCreateGoal(UserModel user) {
        return userGoalRepository.findByUser(user)
                .orElseGet(() -> {
                    UserGoalModel defaultGoal = new UserGoalModel();
                    defaultGoal.setUser(user);
                    defaultGoal.setWeeklyGoal(0.0);
                    defaultGoal.setDailyGoal(0.0);
                    return userGoalRepository.save(defaultGoal);
                });
    }

    private String getStatus(Double weekProgress, Double dailyProgress) {
        if (weekProgress >= 90 && dailyProgress >= 90) return "🟢 Excelente!";
        if (weekProgress >= 70) return "🟡 No caminho!";
        return "🔴 Atrasado!";
    }

    private UserModel getCurrentUser() {
        return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}