package com.j3s.yobuddy.domain.task.repository;

import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.entity.UserTaskStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    List<UserTask> findByUser_UserId(Long userId);

    Optional<UserTask> findByIdAndUser_UserId(Long userTaskId, Long userId);

    List<UserTask> findByUser_UserIdIn(List<Long> userIds);

    Optional<UserTask> findByUser_UserIdAndProgramTask_Id(Long userId, Long programTaskId);

    List<UserTask> findByDeletedFalseAndStatusInAndProgramTask_DueDateBetween(
        List<UserTaskStatus> statuses,
        LocalDateTime start,
        LocalDateTime end
    );

    List<UserTask> findAllByProgramTaskAndDeletedFalse(ProgramTask programTask);
}
