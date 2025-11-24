package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.UserTaskSubmitRequest;

public interface UserTaskCommandService {

    void submitTask(Long userId, Long programTaskId, UserTaskSubmitRequest request);
}

