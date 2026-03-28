package dev.guilherme.demo.topic;

import dev.guilherme.demo.study.DifficultyLevel;
import dev.guilherme.demo.subject.SubjectModel;
import dev.guilherme.demo.subject.SubjectService;
import dev.guilherme.demo.topic.dtos.TopicDTO;
import dev.guilherme.demo.topic.dtos.TopicResponseDTO;
import dev.guilherme.demo.topic.exception.TopicNotFoundException;
import dev.guilherme.demo.user.UserModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicService {

    private final TopicRepository topicRepository;

    private final SubjectService subjectService;

    public TopicResponseDTO save(TopicDTO dto) {
        UserModel user = getCurrentUser();

        SubjectModel subject = subjectService.findById(dto.subject());

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }

        TopicModel topic = TopicModel.builder()
                .name(dto.name())
                .description(dto.description())
                .estimatedHours(dto.estimatedHours() != null ? dto.estimatedHours() : 0.0)
                .difficultyLevel(dto.difficultyLevel() != null ? dto.difficultyLevel() : DifficultyLevel.MEDIUM)
                .subject(subject)
                .user(user)
                .build();

        TopicModel saved = topicRepository.save(topic);
        return toResponse(saved);
    }

    public List<TopicResponseDTO> getBySubject(Long subjectId) {
        UserModel user = getCurrentUser();
        SubjectModel subject = subjectService.findById(subjectId);

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }

        return topicRepository.findBySubjectIdOrderByName(subjectId)
                .stream().map(this::toResponse)
                .toList();
    }

    public TopicResponseDTO complete(Long id) {
        UserModel user = getCurrentUser();
        TopicModel topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException(id));

        if (!topic.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado");
        }

        topic.setCompleted(true);
        return toResponse(topicRepository.save(topic));
    }

    private UserModel getCurrentUser() {
        return (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private TopicResponseDTO toResponse(TopicModel topic) {
        return new TopicResponseDTO(
                topic.getId(),
                topic.getName(),
                topic.getDescription(),
                topic.getEstimatedHours(),
                topic.getDifficultyLevel(),
                topic.getCompleted(),
                topic.getSubject().getId()
        );
    }

    public TopicModel findById(Long id) {
        TopicModel topic = topicRepository.findById(id)
                .orElseThrow(() -> new TopicNotFoundException(id));

        if (!topic.getUser().getId().equals(getCurrentUser().getId())) {
            throw new AccessDeniedException("Acesso negado");
        }
        return topic;
    }

    public TopicResponseDTO update(Long id, TopicDTO dto) {
        TopicModel topic = findById(id);

        topic.setName(dto.name());
        topic.setDescription(dto.description());
        topic.setEstimatedHours(dto.estimatedHours() != null ? dto.estimatedHours() : topic.getEstimatedHours());
        topic.setDifficultyLevel(dto.difficultyLevel() != null ? dto.difficultyLevel() : topic.getDifficultyLevel());

        return toResponse(topicRepository.save(topic));
    }

    public void delete(Long id) {
        TopicModel topic = findById(id);
        topicRepository.delete(topic);
    }


}
