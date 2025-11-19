package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class TaskUpdateResponse {

    private Long taskId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer points;
    private LocalDateTime updatedAt;
}
