package com.j3s.yobuddy.domain.task.repository;

import com.j3s.yobuddy.domain.task.entity.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    Optional<UserTask> findByUser_UserIdAndProgramTask_Id(Long userId, Long programTaskId);

    List<UserTask> findByUser_UserId(Long userId);
}
