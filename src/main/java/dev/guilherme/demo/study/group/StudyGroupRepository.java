package dev.guilherme.demo.study.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroupModel, Long> {
    @Query("SELECT g FROM StudyGroupModel g LEFT JOIN FETCH g.creator WHERE g.creator.id = :creatorId ORDER BY g.createdDate DESC")
    List<StudyGroupModel> findByCreatorIdOrderByCreatedDateDesc(@Param("creatorId") Long creatorId);

    @Query("SELECT g FROM StudyGroupModel g LEFT JOIN FETCH g.creator WHERE g.isPrivate = false")
    List<StudyGroupModel> findByIsPrivateFalse();
}