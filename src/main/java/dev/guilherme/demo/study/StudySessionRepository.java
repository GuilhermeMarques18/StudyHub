package dev.guilherme.demo.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySessionModel, Long> {

    List<StudySessionModel> findByUserId(Long userId);

    List<StudySessionModel> findByUserIdAndSubjectIdOrderByStartTimeDesc(Long userId, Long subjectId);

    List<StudySessionModel> findBySubjectId(Long subjectId);

    @Query("SELECT s.subject.id, SUM(s.durationMinutes) FROM StudySessionModel s WHERE s.subject.id IN :ids GROUP BY s.subject.id")
    List<Object[]> sumDurationBySubjectIds(@Param("ids") List<Long> subjectIds);

    @Query("SELECT SUM(s.durationMinutes) FROM StudySessionModel s " +
            "WHERE s.user.id = :userId " +
            "AND s.startTime >= :startOfWeek " +
            "AND s.startTime < :endOfWeek")
    Integer getWeeklyMinutes(Long userId, LocalDateTime startOfWeek, LocalDateTime endOfWeek);

    @Query("SELECT SUM(s.durationMinutes)/60.0 FROM StudySessionModel s WHERE s.user.id = :userId")
    Double getTotalHours(@Param("userId") Long userId);

    @Query("SELECT SUM(s.durationMinutes) FROM StudySessionModel s " +
            "WHERE s.user.id = :userId " +
            "AND s.startTime >= :startOfDay " +
            "AND s.startTime < :endOfDay")
    Integer getDailyMinutes(@Param("userId") Long userId,
                         @Param("startOfDay") LocalDateTime startOfDay,
                         @Param("endOfDay") LocalDateTime endOfDay);

    Long countByUserId(Long userId);

    @Query("SELECT DISTINCT DATE(s.startTime) FROM StudySessionModel s " +
            "WHERE s.user.id = :userId AND s.startTime >= :limitDate " +
            "ORDER BY DATE(s.startTime) DESC")
    List<java.sql.Date> findStudyDatesByUser(@Param("userId") Long userId, @Param("limitDate") LocalDateTime limitDate);
}