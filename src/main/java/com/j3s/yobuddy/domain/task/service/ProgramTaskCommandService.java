package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.ProgramTaskAssignRequest;
import com.j3s.yobuddy.domain.task.dto.response.ProgramTaskAssignResponse;

public interface ProgramTaskCommandService {

    ProgramTaskAssignResponse assignTask(Long programId, Long taskId, ProgramTaskAssignRequest request);

    void unassignTask(Long programId, Long taskId);
}

