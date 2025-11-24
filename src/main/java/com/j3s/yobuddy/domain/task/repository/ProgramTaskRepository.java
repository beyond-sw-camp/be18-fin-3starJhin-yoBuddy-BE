package com.j3s.yobuddy.domain.task.repository;

import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramTaskRepository extends JpaRepository<ProgramTask, Long> {

    Optional<ProgramTask> findByOnboardingTaskId(Long taskId);

    int countByOnboardingTaskId(Long taskId);

    void deleteByOnboardingTaskId(Long taskId);

    boolean existsByOnboardingProgram_ProgramIdAndOnboardingTask_Id(
        Long programId,
        Long taskId
    );

    List<ProgramTask> findByOnboardingProgram_ProgramId(Long programId);

    Optional<ProgramTask> findByOnboardingProgram_ProgramIdAndOnboardingTask_Id(Long programId, Long taskId);
}

