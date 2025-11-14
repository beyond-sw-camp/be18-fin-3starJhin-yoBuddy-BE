package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class AdminTaskListItem {

    private Long taskId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer points;
    private Integer assignedProgramCount;
    private LocalDateTime createdAt;
}
