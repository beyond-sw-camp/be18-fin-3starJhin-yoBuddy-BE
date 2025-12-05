package com.j3s.yobuddy.domain.weeklyReport.dto.response;


import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeeklyReportSummaryResponse {

    private Long weeklyReportId;
    private Integer weekNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private WeeklyReportStatus status;
    private Long mentorId;
    private Long menteeId;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;

    public static WeeklyReportSummaryResponse from(WeeklyReport report) {
        return WeeklyReportSummaryResponse.builder()
            .weeklyReportId(report.getWeeklyReportId())
            .weekNumber(report.getWeekNumber())
            .startDate(report.getStartDate())
            .endDate(report.getEndDate())
            .status(report.getStatus())
            .mentorId(report.getMentorId())
            .menteeId(report.getMenteeId())
            .createdAt(report.getCreatedAt())
            .submittedAt(report.getSubmittedAt())
            .updatedAt(report.getUpdatedAt())
            .build();
    }
}
