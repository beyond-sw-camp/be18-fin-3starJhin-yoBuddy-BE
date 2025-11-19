package com.j3s.yobuddy.domain.task.dto.request;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class TaskUpdateRequest {

    private String title;
    private String description;
    private Integer points;
    private List<Long> fileIds;
}
