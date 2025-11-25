package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.response.AdminTaskDetailResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskListResponse;

public interface TaskQueryService {
    TaskListResponse getTaskList();

    AdminTaskDetailResponse getTaskDetail(Long taskId);
}
