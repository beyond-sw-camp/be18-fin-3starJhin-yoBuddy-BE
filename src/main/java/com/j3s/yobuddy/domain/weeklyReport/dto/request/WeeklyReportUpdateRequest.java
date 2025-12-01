package com.j3s.yobuddy.domain.weeklyReport.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class WeeklyReportUpdateRequest {

    @NotBlank
    private String accomplishments;

    @NotBlank
    private String challenges;

    @NotBlank
    private String learnings;

    private String status;
}