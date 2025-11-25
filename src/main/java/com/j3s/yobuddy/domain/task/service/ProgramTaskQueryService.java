package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.response.ProgramTaskListResponse;

public interface ProgramTaskQueryService {
    ProgramTaskListResponse getProgramTaskList(Long programId);
}
