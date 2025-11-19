package com.j3s.yobuddy.domain.task.repository;

import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramTaskRepository extends JpaRepository<ProgramTask, Long> {
    Optional<ProgramTask> findByOnboardingTaskId(Long taskId);

    int countByOnboardingTaskId(Long taskId);

    void deleteByOnboardingTaskId(Long taskId);
}
