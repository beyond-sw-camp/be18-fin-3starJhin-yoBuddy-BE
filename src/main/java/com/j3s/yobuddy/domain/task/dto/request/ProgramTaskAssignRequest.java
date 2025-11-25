package com.j3s.yobuddy.domain.task.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ProgramTaskAssignRequest {

    @NotNull
    private LocalDate dueDate;
}
