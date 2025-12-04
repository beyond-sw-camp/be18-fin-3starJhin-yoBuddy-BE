package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.TaskGradeRequest;
import com.j3s.yobuddy.domain.task.dto.response.MentorMenteeTaskResponse;
import com.j3s.yobuddy.domain.task.dto.response.MentorTaskDetailResponse;
import com.j3s.yobuddy.domain.task.dto.response.MentorTaskListResponse;

public interface MentorTaskService {

    MentorTaskListResponse getAllMenteeTasks(Long mentorId);

    MentorTaskDetailResponse getTaskDetail(Long mentorId, Long userTaskId);

    void gradeTask(Long mentorId, Long userTaskId, TaskGradeRequest request);
}
