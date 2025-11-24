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

    private Integer grade;

    private String feedback;
    private LocalDateTime updatedAt;
}
