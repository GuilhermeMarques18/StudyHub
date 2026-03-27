package dev.guilherme.demo.study.group;

import dev.guilherme.demo.group.GroupMember;
import dev.guilherme.demo.user.UserModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "study_group")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
@Builder
public class StudyGroupModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 500)
    private String description;

    private Boolean isPrivate = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private UserModel creator;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<GroupMember> members;
}

