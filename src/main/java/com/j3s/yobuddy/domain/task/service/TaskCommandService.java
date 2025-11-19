package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.TaskCreateRequest;
import com.j3s.yobuddy.domain.task.dto.request.TaskUpdateRequest;
import com.j3s.yobuddy.domain.task.dto.response.TaskCreateResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskDeleteResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskUpdateResponse;

public interface TaskCommandService {
    TaskCreateResponse createTask(TaskCreateRequest request);
    TaskUpdateResponse updateTask(Long taskId, TaskUpdateRequest request);
    TaskDeleteResponse deleteTask(Long taskId);

}
