package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskAssignResponse {

    private Long programId;
    private Long taskId;
    private String title;
    private LocalDateTime assignedAt;
}
