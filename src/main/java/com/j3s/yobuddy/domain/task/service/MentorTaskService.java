package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.TaskGradeRequest;
import com.j3s.yobuddy.domain.task.dto.response.MentorMenteeTaskResponse;

public interface MentorTaskService {

    // 멘토가 멘티의 제출 과제를 채점
    void gradeTask(Long mentorId, Long menteeId, Long programTaskId, TaskGradeRequest request);

    // 멘토가 멘티의 제출한 과제 목록 조회
    MentorMenteeTaskResponse getSubmittedTasks(Long mentorId, Long menteeId);
}
