package com.j3s.yobuddy.domain.weeklyReport.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeeklyReportDetailResponse {

    private Long weeklyReportId;
    private Integer weekNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String accomplishments;
    private String challenges;
    private String learnings;
    private String mentorFeedback;
    private WeeklyReportStatus status;
    private Long mentorId;
    private Long menteeId;
    private LocalDateTime createdAt;
    private LocalDateTime sumbittedAt;
    private LocalDateTime updatedAt;

    public static WeeklyReportDetailResponse from(WeeklyReport report) {
        return WeeklyReportDetailResponse.builder()
            .weeklyReportId(report.getWeeklyReportId())
            .weekNumber(report.getWeekNumber())
            .startDate(report.getStartDate())
            .endDate(report.getEndDate())
            .accomplishments(report.getAccomplishments())
            .challenges(report.getChallenges())
            .learnings(report.getLearnings())
            .mentorFeedback(report.getMentorFeedback())
            .status(report.getStatus())
            .mentorId(report.getMentorId())
            .menteeId(report.getMenteeId())
            .createdAt(report.getCreatedAt())
            .sumbittedAt(report.getSubmittedAt())
            .updatedAt(report.getUpdatedAt())
            .build();
    }
}