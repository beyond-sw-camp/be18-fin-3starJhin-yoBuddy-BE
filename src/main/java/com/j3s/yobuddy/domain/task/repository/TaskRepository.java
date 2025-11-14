package com.j3s.yobuddy.domain.task.repository;

import com.j3s.yobuddy.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {}
