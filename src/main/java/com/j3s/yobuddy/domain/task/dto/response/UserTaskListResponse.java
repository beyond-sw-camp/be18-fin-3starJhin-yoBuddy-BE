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
    private List<TaskInfo> tasks;

    @Getter
    @Builder
    public static class TaskInfo {

        private Long userTaskId;
        private Long taskId;
        private String title;
        private LocalDate dueDate;

        private String status;
        private Integer grade;
        private LocalDateTime submittedAt;
        private String feedback;
    }
}
