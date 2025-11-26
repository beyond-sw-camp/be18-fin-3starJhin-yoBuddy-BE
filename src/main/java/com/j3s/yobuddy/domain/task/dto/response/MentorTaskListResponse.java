package com.j3s.yobuddy.domain.task.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentorTaskListResponse {

    private Long mentorId;
    private List<MenteeTaskInfo> tasks;

    @Getter
    @Builder
    public static class MenteeTaskInfo {
        private Long userTaskId;
        private Long menteeId;
        private String menteeName;

        private Long onboardingTaskId;
        private String taskTitle;
        private LocalDate dueDate;

        private String status;
        private Integer grade;
        private LocalDateTime submittedAt;
        private String feedback;
    }
}
