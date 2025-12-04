package com.j3s.yobuddy.domain.task.service;

import java.math.BigDecimal;

import com.j3s.yobuddy.domain.task.dto.response.UserTaskDetailResponse;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskListResponse;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskScoreResponse;

public interface UserTaskQueryService {

    UserTaskListResponse getUserTaskList(Long userId);

    UserTaskDetailResponse getUserTaskDetail(Long userId, Long userTaskId);

    UserTaskScoreResponse getUserTaskScore(Long userId, Long userTaskId);

    BigDecimal calculateCompletionRate(Long userId);

    BigDecimal calculateTaskScore(Long userId);
}
