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

    /**
     * "DRAFT" 또는 "SUBMITTED"만 받는다고 가정
     */
    private String status;
}