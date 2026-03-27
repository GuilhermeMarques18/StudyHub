package dev.guilherme.demo.group;

import dev.guilherme.demo.study.group.StudyGroupModel;
import dev.guilherme.demo.user.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroupModel group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(name = "joined_date")
    private LocalDateTime joinedDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Role role = Role.MEMBER;

    public enum Role {
        ADMIN, MEMBER
    }
}