package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MentorMenteeTaskResponse {

    private Long menteeId;
    private List<TaskInfo> tasks;

    @Getter
    @Builder
    public static class TaskInfo {
        private Long programTaskId;
        private Long taskId;          // OnboardingTask ID
        private String title;         // 과제 제목
        private LocalDate dueDate;
        private String status;        // GRADED, SUBMITTED, LATE 등
        private Integer grade;
        private LocalDateTime submittedAt;
        private String feedback;
    }
}
