package com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response;


import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentorWeeklyReportSummaryResponse {

    private Long weeklyReportId;
    private Long menteeId;
    private String menteeName;
    private Integer weekNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private WeeklyReportStatus status;
    private LocalDateTime updatedAt;

    public static MentorWeeklyReportSummaryResponse from(WeeklyReport report,
        String menteeName) {
        return MentorWeeklyReportSummaryResponse.builder()
            .weeklyReportId(report.getWeeklyReportId())
            .menteeId(report.getMenteeId())
            .menteeName(menteeName)
            .weekNumber(report.getWeekNumber())
            .startDate(report.getStartDate())
            .endDate(report.getEndDate())
            .status(report.getStatus())
            .updatedAt(report.getUpdatedAt())
            .build();
    }
}
