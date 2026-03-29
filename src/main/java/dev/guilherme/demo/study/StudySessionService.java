package dev.guilherme.demo.study;

import dev.guilherme.demo.study.dtos.StudySessionResponseDTO;
import dev.guilherme.demo.study.exception.StudySessionNotFoundException;
import dev.guilherme.demo.study.exception.TopicSubjectMismatchException;
import dev.guilherme.demo.subject.SubjectService;
import dev.guilherme.demo.study.dtos.StudySessionDTO;
import dev.guilherme.demo.subject.SubjectModel;
import dev.guilherme.demo.topic.TopicModel;
import dev.guilherme.demo.topic.TopicService;
import dev.guilherme.demo.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final SubjectService subjectService;
    private final TopicService topicService;

    public StudySessionResponseDTO createSession(StudySessionDTO dto) {
        UserModel currentUser = getCurrentUser();
        SubjectModel subject = subjectService.findById(dto.subjectId());
        TopicModel topicModel = getValidTopicOrNull(dto.topicId(), dto.subjectId());

        StudySessionModel session = new StudySessionModel();
        updateSessionFields(session, dto, subject, topicModel);
        session.setUser(currentUser);

        StudySessionModel saved = studySessionRepository.save(session);
        return toResponseDTO(saved);
    }

    public StudySessionResponseDTO updateSession(Long id, StudySessionDTO dto) {
        StudySessionModel session = findById(id);

        SubjectModel subject = subjectService.findById(dto.subjectId());
        TopicModel topicModel = getValidTopicOrNull(dto.topicId(), dto.subjectId());

        updateSessionFields(session, dto, subject, topicModel);

        StudySessionModel updatedSession = studySessionRepository.save(session);
        return toResponseDTO(updatedSession);
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
        subjectService.findById(subjectId);
        return studySessionRepository.findByUserIdAndSubjectIdOrderByStartTimeDesc(
                        currentUser.getId(), subjectId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private UserModel getCurrentUser() {
        return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public void delete(Long id) {
        StudySessionModel session = findById(id);
        studySessionRepository.delete(session);
    }

    public StudySessionModel findById(Long id) {
        StudySessionModel study = studySessionRepository.findById(id)
                .orElseThrow(() -> new StudySessionNotFoundException(id));

        UserModel user = getCurrentUser();

        if(!study.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }
        return study;
    }

    private TopicModel getValidTopicOrNull(Long topicId, Long subjectId) {
        if (topicId == null) {
            return null;
        }
        TopicModel topicModel = topicService.findById(topicId);
        if (!topicModel.getSubject().getId().equals(subjectId)) {
            throw new TopicSubjectMismatchException("Tópico não pertence à matéria");
        }
        return topicModel;
    }

    private void updateSessionFields(StudySessionModel session, StudySessionDTO dto, SubjectModel subject, TopicModel topicModel) {
        session.setStartTime(dto.startTime());
        session.setEndTime(dto.endTime());
        session.setDurationMinutes(dto.durationMinutes());
        session.setDifficultyLevel(dto.difficultyLevel());
        session.setNotes(dto.notes());
        session.setTopicModel(topicModel);
        session.setSubject(subject);
    }

    private StudySessionResponseDTO toResponseDTO(StudySessionModel session) {
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