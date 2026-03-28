package dev.guilherme.demo.subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubjectRepository extends JpaRepository<SubjectModel, Long> {
    List<SubjectModel> findByUserIdOrderByName(Long userId);

}
