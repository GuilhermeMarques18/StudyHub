package dev.guilherme.demo.user.usergoal;

import dev.guilherme.demo.user.UserModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_goal")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserGoalModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserModel user;

    @DecimalMin("0.0")
    @Column(name =  "weekly_goal")
    private Double weeklyGoal;

    @DecimalMin("0.0")
    @Column(name = "daily_goal")
    private Double dailyGoal;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
