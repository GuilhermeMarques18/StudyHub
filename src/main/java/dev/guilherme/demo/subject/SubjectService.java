package dev.guilherme.demo.subject;

import dev.guilherme.demo.study.StudySessionRepository;
import dev.guilherme.demo.subject.dtos.SubjectDTO;
import dev.guilherme.demo.subject.dtos.SubjectResponseDTO;
import dev.guilherme.demo.user.UserModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final StudySessionRepository studySessionRepository;

    @Transactional
    public SubjectResponseDTO save(SubjectDTO dto) {
        UserModel user = getCurrentUser();

        SubjectModel subject = SubjectModel.builder()
                .name(dto.name())
                .description(dto.description())
                .goal(dto.goal() != null ? dto.goal() : 0.0)
                .color("#3498db")
                .user(user)
                .build();

        SubjectModel saved = subjectRepository.save(subject);
        return toResponse(saved);
    }

    public List<SubjectResponseDTO> getAll() {
        UserModel user = getCurrentUser();
        List<SubjectModel> subjects = subjectRepository.findByUserIdOrderByName(user.getId());
        return subjects.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private SubjectResponseDTO toResponse(SubjectModel subject) {
        double hoursStudied = studySessionRepository.findBySubjectId(subject.getId())
                .stream()
                .mapToDouble(s -> s.getDurationMinutes() / 60.0)
                .sum();

        double progress = subject.getGoal() > 0
                ? (hoursStudied / subject.getGoal()) * 100
                : 0.0;

        return new SubjectResponseDTO(
                subject.getId(),
                subject.getName(),
                subject.getDescription(),
                subject.getGoal(),
                subject.getColor(),
                hoursStudied,
                Math.min(progress, 100.0)
        );
    }

    private UserModel getCurrentUser() {
        return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}