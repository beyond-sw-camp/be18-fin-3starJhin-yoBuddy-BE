package com.j3s.yobuddy.domain.training.repository;

import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.Training;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("""
        SELECT DISTINCT t
        FROM Training t
        LEFT JOIN ProgramTraining pt ON pt.training = t
        LEFT JOIN pt.program p
        WHERE t.deleted = false
          AND (:type IS NULL OR t.type = :type)
          AND (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:programId IS NULL 
               OR (p.programId = :programId AND (p.deleted IS NULL OR p.deleted = false)))
        """)
    Page<Training> searchTrainings(
        @Param("type") TrainingType type,
        @Param("programId") Long programId,
        @Param("keyword") String keyword,
        Pageable pageable
    );
}
