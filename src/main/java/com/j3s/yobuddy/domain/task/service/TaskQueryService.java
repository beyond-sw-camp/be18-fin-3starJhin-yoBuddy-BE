package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.response.AdminTaskDetailResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskListResponse;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskListResponse;

public interface TaskQueryService {
    TaskListResponse getTaskList();
    
    UserTaskListResponse getUserTaskList(Long userId);

    AdminTaskDetailResponse getTaskDetail(Long taskId);
}
