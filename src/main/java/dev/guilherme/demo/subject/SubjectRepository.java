package dev.guilherme.demo.subject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<SubjectModel, Long> {
    List<SubjectModel> findByUserIdOrderByName(Long userId);
}
