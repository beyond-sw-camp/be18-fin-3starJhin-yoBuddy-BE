package com.j3s.yobuddy.domain.weeklyReport.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.j3s.yobuddy.domain.weeklyReport.dto.request.WeeklyReportUpdateRequest;
import com.j3s.yobuddy.domain.weeklyReport.dto.response.WeeklyReportDetailResponse;
import com.j3s.yobuddy.domain.weeklyReport.dto.response.WeeklyReportSummaryResponse;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;

public interface WeeklyReportService {

    Page<WeeklyReportSummaryResponse> getWeeklyReports(Long menteeId,
        WeeklyReportStatus status,
        Pageable pageable);

    WeeklyReportDetailResponse getWeeklyReportDetail(Long menteeId, Long weeklyReportId);

    WeeklyReportDetailResponse updateWeeklyReport(Long menteeId,
        Long weeklyReportId,
        WeeklyReportUpdateRequest request);
}
