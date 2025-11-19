package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserTaskListResponse {

    private Long userId;
    private Long programId;

    private List<TaskSummary> tasks;

    @Getter
    @Builder
    public static class TaskSummary {

        private Long taskId;
        private String title;
        private LocalDate dueDate;

        private String status;         // PENDING, SUBMITTED, GRADED, LATE
        private Integer grade;         // null 가능
        private LocalDateTime submittedAt;  // null 가능

        private String feedback;       // 채점 후 제공
    }
}
