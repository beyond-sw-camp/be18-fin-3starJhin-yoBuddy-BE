package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserTaskScoreResponse {

    private Long userId;
    private Long taskId;

    private String title;

    private String status;      // PENDING, SUBMITTED, GRADED
    private Integer grade;

    private String feedback;
    private String evaluator;

    private LocalDateTime evaluatedAt;
    private LocalDateTime updatedAt;

}
