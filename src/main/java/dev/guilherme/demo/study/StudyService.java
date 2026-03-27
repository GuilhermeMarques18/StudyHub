package dev.guilherme.demo.study;

import dev.guilherme.demo.study.dtos.StudySessionResponseDTO;
import dev.guilherme.demo.topic.TopicRepository;
import dev.guilherme.demo.study.dtos.StudySessionDTO;
import dev.guilherme.demo.subject.SubjectModel;
import dev.guilherme.demo.subject.SubjectRepository;
import dev.guilherme.demo.topic.TopicModel;
import dev.guilherme.demo.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {

    private final StudySessionRepository studySessionRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;

    public StudySessionResponseDTO createSession(StudySessionDTO dto) {
        UserModel currentUser = getCurrentUser();

        SubjectModel subject = subjectRepository.findById(dto.subjectId())
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        if (!subject.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Acesso negado à matéria");
        }

        TopicModel topicModel = null;
        if (dto.topicId() != null) {
            topicModel = topicRepository.findById(dto.topicId())
                    .orElseThrow(() -> new RuntimeException("Tópico não encontrado"));
            if (!topicModel.getSubject().getId().equals(dto.subjectId())) {
                throw new RuntimeException("Tópico não pertence à matéria");
            }
        }

        StudySession session = new StudySession();
        session.setStartTime(dto.startTime());
        session.setEndTime(dto.endTime());
        session.setDurationMinutes(dto.durationMinutes());
        session.setDifficultyLevel(dto.difficultyLevel());
        session.setNotes(dto.notes());
        session.setTopicModel(topicModel);
        session.setSubject(subject);
        session.setUser(currentUser);

        StudySession saved = studySessionRepository.save(session);
        return toResponseDTO(saved);
    }

    public List<StudySessionResponseDTO> getSessionsByUser() {
        UserModel currentUser = getCurrentUser();
        return studySessionRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<StudySessionResponseDTO> getSessionsBySubject(Long subjectId) {
        UserModel currentUser = getCurrentUser();
        return studySessionRepository.findByUserIdAndSubjectIdOrderByStartTimeDesc(
                        currentUser.getId(), subjectId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private UserModel getCurrentUser() {
        return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private StudySessionResponseDTO toResponseDTO(StudySession session) {
        return new StudySessionResponseDTO(
                session.getId(),
                session.getStartTime(),
                session.getEndTime(),
                session.getDurationMinutes(),
                session.getDifficultyLevel(),
                session.getNotes(),
                session.getTopicModel() != null ? session.getTopicModel().getId() : null,
                session.getSubject().getId(),
                session.getSubject().getName(),
                session.getCreatedDate()
        );
    }
}