package com.j3s.yobuddy.domain.mentor.weeklyReport.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.request.MentorWeeklyReportFeedbackRequest;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response.MentorWeeklyReportDetailResponse;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response.MentorWeeklyReportSummaryResponse;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;

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
    List<MentorWeeklyReportDetailResponse> getWeeklyReportsByUserId(String userId);
}
