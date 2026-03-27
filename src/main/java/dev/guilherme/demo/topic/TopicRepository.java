package dev.guilherme.demo.topic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<TopicModel, Long> {
    List<TopicModel> findBySubjectIdOrderByName(Long subjectId);
}