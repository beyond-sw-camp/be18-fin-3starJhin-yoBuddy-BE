package com.j3s.yobuddy.domain.task.repository;

import com.j3s.yobuddy.domain.task.entity.OnboardingTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<OnboardingTask, Long> {}
