package com.j3s.yobuddy.domain.task.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class TaskCreateResponse {

    private Long taskId;
    private String title;
    private String description;
    private Integer points;
    private List<Long> departmentIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
