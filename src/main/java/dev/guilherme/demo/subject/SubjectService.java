package dev.guilherme.demo.subject;

import dev.guilherme.demo.study.StudySessionRepository;
import dev.guilherme.demo.subject.dtos.SubjectDTO;
import dev.guilherme.demo.subject.dtos.SubjectResponseDTO;
import dev.guilherme.demo.subject.exception.SubjectNotFoundException;
import dev.guilherme.demo.user.UserModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final StudySessionRepository studySessionRepository;

    public SubjectResponseDTO save(SubjectDTO dto) {
        UserModel user = getCurrentUser();

        SubjectModel subject = SubjectModel.builder()
                .name(dto.name())
                .description(dto.description())
                .goal(dto.goal() != null ? dto.goal() : 0.0)
                .color(dto.color() != null ? dto.color() : "#3498db")
                .user(user)
                .build();

        SubjectModel saved = subjectRepository.save(subject);
        return toResponse(saved, 0.0);
    }

    public List<SubjectResponseDTO> getAll() {
        UserModel user = getCurrentUser();
        List<SubjectModel> subjects = subjectRepository.findByUserIdOrderByName(user.getId());

        List<Long> ids = subjects.stream().map(SubjectModel::getId).toList();

        Map<Long, Double> minutesById = studySessionRepository
                .sumDurationBySubjectIds(ids)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Number) row[1]).doubleValue()
                ));

        return subjects.stream()
                .map(s -> toResponse(s, minutesById.getOrDefault(s.getId(), 0.0)))
                .toList();
    }

    private SubjectResponseDTO toResponse(SubjectModel subject, double totalMinutes) {
        double hoursStudied = totalMinutes / 60.0;
        double progress = subject.getGoal() > 0 ? (hoursStudied / subject.getGoal()) * 100 : 0.0;

        return new SubjectResponseDTO(
                subject.getId(), subject.getName(), subject.getDescription(),
                subject.getGoal(), subject.getColor(),
                hoursStudied, Math.min(progress, 100.0)
        );
    }

    public SubjectResponseDTO getById(Long id) {
        SubjectModel subject = findById(id);
        double totalMinutes = studySessionRepository.findBySubjectId(id)
                .stream()
                .mapToDouble(s -> s.getDurationMinutes())
                .sum();
        return toResponse(subject, totalMinutes);
    }


    public SubjectModel findById(Long id) {
        SubjectModel subject = subjectRepository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException(id));

        UserModel user = getCurrentUser();
        if (!subject.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }
        return subject;
    }

    public SubjectResponseDTO update(Long id, SubjectDTO dto) {
        SubjectModel subject = findById(id);

        subject.setName(dto.name());
        subject.setDescription(dto.description());
        subject.setGoal(dto.goal() != null ? dto.goal() : 0.0);
        subject.setColor(dto.color() != null ? dto.color() : subject.getColor());

        return toResponse(subjectRepository.save(subject), 0.0);
    }

    private UserModel getCurrentUser() {
        return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public void delete(Long id) {
        SubjectModel subject = findById(id);
        subjectRepository.delete(subject);
    }
}