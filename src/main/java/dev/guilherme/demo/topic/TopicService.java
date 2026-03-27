package dev.guilherme.demo.topic;

import dev.guilherme.demo.study.DifficultyLevel;
import dev.guilherme.demo.subject.SubjectModel;
import dev.guilherme.demo.subject.SubjectRepository;
import dev.guilherme.demo.topic.dtos.TopicDTO;
import dev.guilherme.demo.topic.dtos.TopicResponseDTO;
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
public class TopicService {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    public TopicResponseDTO save(TopicDTO dto) {
        UserModel user = getCurrentUser();

        SubjectModel subject = subjectRepository.findById(dto.subject())
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        if (!subject.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
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
        return topicRepository.findBySubjectIdOrderByName(subjectId)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TopicResponseDTO complete(Long id) {
        UserModel user = getCurrentUser();
        TopicModel topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tópico não encontrado"));

        if (!topic.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
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
}
