package com.j3s.yobuddy.domain.weeklyReport.service;


import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.j3s.yobuddy.domain.weeklyReport.dto.request.WeeklyReportUpdateRequest;
import com.j3s.yobuddy.domain.weeklyReport.dto.response.WeeklyReportDetailResponse;
import com.j3s.yobuddy.domain.weeklyReport.dto.response.WeeklyReportSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WeeklyReportService {

    Page<WeeklyReportSummaryResponse> getWeeklyReports(Long menteeId,
        WeeklyReportStatus status,
        Pageable pageable);

    WeeklyReportDetailResponse getWeeklyReportDetail(Long menteeId, Long weeklyReportId);

    WeeklyReportDetailResponse updateWeeklyReport(Long menteeId,
        Long weeklyReportId,
        WeeklyReportUpdateRequest request);
}
