package com.j3s.yobuddy.domain.task.repository;

import com.j3s.yobuddy.domain.task.entity.TaskDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDepartmentRepository extends JpaRepository<TaskDepartment, Long> {
    void deleteByTask_Id(Long taskId);
}