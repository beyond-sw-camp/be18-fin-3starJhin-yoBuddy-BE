package com.j3s.yobuddy.domain.training.repository;

import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramTrainingRepository extends JpaRepository<ProgramTraining, Long> {

    List<ProgramTraining> findByTraining_TrainingIdIn(Collection<Long> training_trainingId);

    List<ProgramTraining> findByTraining_TrainingId(Long trainingId);

    long countByTraining_TrainingIdAndProgram_DeletedFalse(Long trainingId);

    boolean existsByProgram_ProgramIdAndTraining_TrainingId(Long programId, Long trainingId);

    Optional<ProgramTraining> findByProgram_ProgramIdAndTraining_TrainingId(Long programId, Long trainingId);
}