package com.j3s.yobuddy.domain.task.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class TaskCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Integer points;

    @NotNull
    private List<Long> departmentIds;

    private List<Long> fileIds;

}