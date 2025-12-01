package com.j3s.yobuddy.domain.weeklyReport.service;

import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.j3s.yobuddy.domain.weeklyReport.exception.WeeklyReportAccessDeniedException;
import com.j3s.yobuddy.domain.weeklyReport.exception.WeeklyReportNotFoundException;
import com.j3s.yobuddy.domain.weeklyReport.exception.WeeklyReportUpdateNotAllowedException;
import com.j3s.yobuddy.domain.weeklyReport.repository.WeeklyReportRepository;
import com.j3s.yobuddy.domain.weeklyReport.dto.request.WeeklyReportUpdateRequest;
import com.j3s.yobuddy.domain.weeklyReport.dto.response.WeeklyReportDetailResponse;
import com.j3s.yobuddy.domain.weeklyReport.dto.response.WeeklyReportSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WeeklyReportServiceImpl implements WeeklyReportService {

    private final WeeklyReportRepository weeklyReportRepository; // ✅ 이것만 주입

    /**
     * [멘티] 주간 리포트 목록 조회 → QueryDSL 메서드도 WeeklyReportRepository를 통해 호출
     */
    @Override
    @Transactional(readOnly = true)
    public Page<WeeklyReportSummaryResponse> getWeeklyReports(Long menteeId,
        WeeklyReportStatus status,
        Pageable pageable) {

        Page<WeeklyReport> page =
            weeklyReportRepository.findWeeklyReports(menteeId, status, pageable);

        return page.map(WeeklyReportSummaryResponse::from);
    }

    /**
     * [멘티] 주간 리포트 상세 조회
     */
    @Override
    @Transactional(readOnly = true)
    public WeeklyReportDetailResponse getWeeklyReportDetail(Long menteeId,
        Long weeklyReportId) {

        WeeklyReport report = weeklyReportRepository.findById(weeklyReportId)
            .orElseThrow(() -> new WeeklyReportNotFoundException(weeklyReportId));

        if (!report.getMenteeId().equals(menteeId)) {
            throw new WeeklyReportAccessDeniedException(menteeId);
        }

        return WeeklyReportDetailResponse.from(report);
    }

    /**
     * [멘티] 주간 리포트 작성/수정
     */
    @Override
    @Transactional
    public WeeklyReportDetailResponse updateWeeklyReport(Long menteeId,
        Long weeklyReportId,
        WeeklyReportUpdateRequest request) {

        WeeklyReport report = weeklyReportRepository.findById(weeklyReportId)
            .orElseThrow(() -> new WeeklyReportNotFoundException(weeklyReportId));

        if (!report.getMenteeId().equals(menteeId)) {
            throw new WeeklyReportAccessDeniedException(menteeId);
        }

        if (report.getStatus() == WeeklyReportStatus.OVERDUE ||
            report.getStatus() == WeeklyReportStatus.REVIEWED) {
            throw new WeeklyReportUpdateNotAllowedException();
        }

        WeeklyReportStatus newStatus = WeeklyReportStatus.SUBMITTED;
        if (request.getStatus() != null) {
            newStatus = WeeklyReportStatus.valueOf(request.getStatus());
        }

        report.updateContent(
            request.getAccomplishments(),
            request.getChallenges(),
            request.getLearnings(),
            newStatus
        );

        return WeeklyReportDetailResponse.from(report);
    }
}