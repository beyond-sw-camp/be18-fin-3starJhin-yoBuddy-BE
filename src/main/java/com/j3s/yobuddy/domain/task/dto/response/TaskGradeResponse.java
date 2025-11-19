package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskGradeResponse {

    private Long userTaskId;
    private Long taskId;
    private Long menteeId;

    private Integer grade;
    private String feedback;
    private String status;

    private LocalDateTime updatedAt;
}
