package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProgramTaskListResponse {

    private Long programId;
    private String programName;
    private Integer totalCount;

    private List<TaskInfo> tasks;

    @Getter
    @Builder
    public static class TaskInfo {
        private Long taskId;
        private String title;
        private String description;
        private LocalDate dueDate;
        private Integer points;
        private LocalDateTime assignedAt;
    }
}
