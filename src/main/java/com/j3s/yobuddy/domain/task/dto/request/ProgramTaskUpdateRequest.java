package com.j3s.yobuddy.domain.task.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProgramTaskUpdateRequest {

    private final LocalDateTime dueDate;

}
