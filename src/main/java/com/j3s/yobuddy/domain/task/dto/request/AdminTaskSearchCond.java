package com.j3s.yobuddy.domain.task.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class AdminTaskSearchCond {

    private Long programId;
    private String keyword;
    private String status;
    private LocalDate dueBefore;
    private LocalDate dueAfter;
}
