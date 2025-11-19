package com.j3s.yobuddy.domain.task.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TaskAssignRequest {
    private LocalDateTime assignedAt; // null이면 서비스에서 now()로 대체
}
