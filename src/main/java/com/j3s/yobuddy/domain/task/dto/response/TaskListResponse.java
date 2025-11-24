package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TaskListResponse {

    private Integer totalCount;
    private List<TaskSummary> tasks;

    @Getter
    @Builder
    public static class TaskSummary {
        private Long taskId;
        private String title;
        private String description;
        private Integer points;
        private LocalDateTime createdAt;
    }
}
