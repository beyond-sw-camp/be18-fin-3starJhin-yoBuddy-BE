package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.TaskSubmitRequest;

public interface UserTaskCommandService {
    void submitTaskWithFiles(
        Long userId,
        Long userTaskId,
        TaskSubmitRequest request
    ) throws Exception;
}
