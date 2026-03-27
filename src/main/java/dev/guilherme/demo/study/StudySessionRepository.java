package dev.guilherme.demo.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    List<StudySession> findByUserId(Long userId);

    List<StudySession> findByUserIdAndSubjectIdOrderByStartTimeDesc(Long userId, Long subjectId);

    List<StudySession> findBySubjectId(Long subjectId);

    @Query("SELECT SUM(s.durationMinutes) FROM StudySession s " +
            "WHERE s.user.id = :userId " +
            "AND s.startTime >= :startOfWeek " +
            "AND s.startTime < :endOfWeek")
    Integer getWeeklyMinutes(Long userId, LocalDateTime startOfWeek, LocalDateTime endOfWeek);

    @Query("SELECT SUM(s.durationMinutes)/60.0 FROM StudySession s WHERE s.user.id = :userId")
    Double getTotalHours(@Param("userId") Long userId);

    @Query("SELECT SUM(s.durationMinutes) FROM StudySession s " +
            "WHERE s.user.id = :userId " +
            "AND s.startTime >= :startOfDay " +
            "AND s.startTime < :endOfDay")
    Integer getDailyMinutes(@Param("userId") Long userId,
                         @Param("startOfDay") LocalDateTime startOfDay,
                         @Param("endOfDay") LocalDateTime endOfDay);
}