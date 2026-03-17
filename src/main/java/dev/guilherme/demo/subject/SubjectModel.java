package dev.guilherme.demo.subject;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import dev.guilherme.demo.user.UserModel;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class SubjectModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @DecimalMin("0.0")
    @Column(name = "goal")
    private Double goal = 0.0;

    @Size(max = 7)
    private String color = "#3498db";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;
}