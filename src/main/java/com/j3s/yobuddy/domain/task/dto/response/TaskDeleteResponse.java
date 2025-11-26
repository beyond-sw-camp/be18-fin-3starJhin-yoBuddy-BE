package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskDeleteResponse {

    private String message;
    private Long taskId;
    private LocalDateTime deletedAt;
    private int programUnlinkedCount;

    public static TaskDeleteResponse of(Long taskId, int count) {
        return TaskDeleteResponse.builder()
            .message("Task deleted successfully")
            .taskId(taskId)
            .programUnlinkedCount(count)
            .deletedAt(LocalDateTime.now())
            .build();
    }
}