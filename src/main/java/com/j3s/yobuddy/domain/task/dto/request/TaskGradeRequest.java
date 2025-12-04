package com.j3s.yobuddy.domain.task.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TaskGradeRequest {

    private Integer grade;     // required
    private String feedback;   // required
}
