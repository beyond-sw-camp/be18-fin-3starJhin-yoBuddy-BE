package com.j3s.yobuddy.domain.mentor.weeklyReport.service;

import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.request.MentorWeeklyReportFeedbackRequest;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response.MentorWeeklyReportDetailResponse;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response.MentorWeeklyReportSummaryResponse;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MentorWeeklyReportService {

    Page<MentorWeeklyReportSummaryResponse> getMenteeWeeklyReports(Long mentorId,
        Long menteeId,
        WeeklyReportStatus status,
        Integer weekNumber,
        Pageable pageable);

    MentorWeeklyReportDetailResponse getWeeklyReportDetail(Long mentorId,
        Long menteeId,
        Long weeklyReportId);

    MentorWeeklyReportDetailResponse submitFeedback(Long mentorId,
        Long menteeId,
        Long weeklyReportId,
        MentorWeeklyReportFeedbackRequest request);
}
