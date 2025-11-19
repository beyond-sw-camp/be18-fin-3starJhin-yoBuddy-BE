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

    private RelatedEntities relatedEntities;

    @Getter
    @Builder
    public static class RelatedEntities {
        private Integer programUnlinkedCount;
        private Integer userTaskRemovedCount;
    }
}