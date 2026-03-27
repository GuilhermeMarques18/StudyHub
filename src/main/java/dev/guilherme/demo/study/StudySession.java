package dev.guilherme.demo.study;

import dev.guilherme.demo.subject.SubjectModel;
import dev.guilherme.demo.topic.TopicModel;
import dev.guilherme.demo.user.UserModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "study")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudySession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @NotNull
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "notes", length = 1000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private TopicModel topicModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectModel subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    private DifficultyLevel  difficultyLevel;


}
