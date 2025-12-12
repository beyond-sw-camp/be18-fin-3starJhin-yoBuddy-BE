package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.ProgramTaskAssignRequest;
import com.j3s.yobuddy.domain.task.dto.request.ProgramTaskUpdateRequest;
import com.j3s.yobuddy.domain.task.dto.response.ProgramTaskAssignResponse;
import com.j3s.yobuddy.domain.task.dto.response.ProgramTaskUpdateResponse;
import com.j3s.yobuddy.domain.task.entity.OnboardingTask;
import com.j3s.yobuddy.domain.user.entity.User;
import java.util.List;

public interface ProgramTaskCommandService {

    ProgramTaskAssignResponse assignTask(Long programId, Long taskId,
        ProgramTaskAssignRequest request);

    void unassignTask(Long programId, Long taskId);

    void notifyNewProgramTask(OnboardingTask task, List<User> enrolledUsers);

    ProgramTaskUpdateResponse updateTask(Long programId, Long taskId,
        ProgramTaskUpdateRequest request);
}

