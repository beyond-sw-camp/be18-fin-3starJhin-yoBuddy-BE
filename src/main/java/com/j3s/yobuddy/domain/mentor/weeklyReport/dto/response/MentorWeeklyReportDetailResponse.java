package com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response;

import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentorWeeklyReportDetailResponse {

    private Long weeklyReportId;
    private Long menteeId;
    private String menteeName;
    private Integer weekNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String accomplishments;
    private String challenges;
    private String learnings;
    private String mentorFeedback;
    private WeeklyReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MentorWeeklyReportDetailResponse from(WeeklyReport report,
        String menteeName) {

        return MentorWeeklyReportDetailResponse.builder()
            .weeklyReportId(report.getWeeklyReportId())
            .menteeId(report.getMenteeId())
            .menteeName(menteeName)
            .weekNumber(report.getWeekNumber())
            .startDate(report.getStartDate())
            .endDate(report.getEndDate())
            .accomplishments(report.getAccomplishments())
            .challenges(report.getChallenges())
            .learnings(report.getLearnings())
            .mentorFeedback(report.getMentorFeedback())
            .status(report.getStatus())
            .createdAt(report.getCreatedAt())
            .updatedAt(report.getUpdatedAt())
            .build();
    }
}
