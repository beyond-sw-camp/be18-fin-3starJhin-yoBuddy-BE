package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.response.UserTaskListResponse;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskScoreResponse;
import com.j3s.yobuddy.domain.task.entity.UserTaskStatus;

public interface UserTaskQueryService {

    UserTaskListResponse getUserTasks(
        Long userId,
        UserTaskStatus status,
        Long programId,
        Boolean overdue
    );

    UserTaskScoreResponse getUserTaskScore(Long userId, Long programTaskId);

}
