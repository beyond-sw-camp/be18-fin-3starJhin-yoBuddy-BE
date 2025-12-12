package com.j3s.yobuddy.domain.task.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ProgramTaskAssignRequest {

    private LocalDateTime assignedAt;
    @NotNull
    private LocalDate dueDate;
}
