package com.j3s.yobuddy.domain.task.repository;

import com.j3s.yobuddy.domain.task.entity.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    List<UserTask> findByUser_UserId(Long userId);

    Optional<UserTask> findByIdAndUser_UserId(Long userTaskId, Long userId);

    List<UserTask> findByUser_UserIdIn(List<Long> userIds);
}