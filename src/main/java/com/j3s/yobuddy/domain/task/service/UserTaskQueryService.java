package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.response.UserTaskDetailResponse;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskListResponse;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskScoreResponse;
import com.j3s.yobuddy.domain.task.entity.UserTaskStatus;

public interface UserTaskQueryService {

    UserTaskListResponse getUserTaskList(Long userId);

    UserTaskDetailResponse getUserTaskDetail(Long userId, Long userTaskId);

    UserTaskScoreResponse getUserTaskScore(Long userId, Long userTaskId);
}
