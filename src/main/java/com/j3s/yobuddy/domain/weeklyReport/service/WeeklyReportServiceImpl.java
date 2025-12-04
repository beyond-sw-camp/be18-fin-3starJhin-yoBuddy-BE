package com.j3s.yobuddy.domain.weeklyReport.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import com.j3s.yobuddy.domain.weeklyReport.dto.request.WeeklyReportUpdateRequest;
import com.j3s.yobuddy.domain.weeklyReport.dto.response.WeeklyReportDetailResponse;
import com.j3s.yobuddy.domain.weeklyReport.dto.response.WeeklyReportSummaryResponse;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.j3s.yobuddy.domain.weeklyReport.exception.WeeklyReportAccessDeniedException;
import com.j3s.yobuddy.domain.weeklyReport.exception.WeeklyReportNotFoundException;
import com.j3s.yobuddy.domain.weeklyReport.exception.WeeklyReportUpdateNotAllowedException;
import com.j3s.yobuddy.domain.weeklyReport.repository.WeeklyReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeeklyReportServiceImpl implements WeeklyReportService {

    private final WeeklyReportRepository weeklyReportRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<WeeklyReportSummaryResponse> getWeeklyReports(Long menteeId,
            WeeklyReportStatus status,
            Pageable pageable) {

        Page<WeeklyReport> page = weeklyReportRepository.findWeeklyReports(menteeId, status, pageable);

        return page.map(WeeklyReportSummaryResponse::from);
    }

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
                report.getStatus() == WeeklyReportStatus.REVIEWED
                || report.getStatus() == WeeklyReportStatus.FEEDBACK_OVERDUE) {
            throw new WeeklyReportUpdateNotAllowedException();
        }

        WeeklyReportStatus previousStatus = report.getStatus();

        WeeklyReportStatus newStatus = WeeklyReportStatus.SUBMITTED;
        if (request.getStatus() != null) {
            newStatus = WeeklyReportStatus.valueOf(request.getStatus());
        }

        report.updateContent(
                request.getAccomplishments(),
                request.getChallenges(),
                request.getLearnings(),
                newStatus,
                LocalDateTime.now());

        if (previousStatus != WeeklyReportStatus.SUBMITTED && newStatus == WeeklyReportStatus.SUBMITTED) {
            sendMentorSubmissionNotification(report);
        }

        return WeeklyReportDetailResponse.from(report);
    }

    private void sendMentorSubmissionNotification(WeeklyReport report) {
        Long mentorId = report.getMentorId();
        if (mentorId == null) {
            return;
        }

        userRepository.findById(mentorId)
                .filter(user -> !user.isDeleted() && user.getRole() == Role.MENTOR)
                .ifPresent(mentor -> notificationService.notify(
                        mentor,
                        NotificationType.MENTOR_WEEKLY_REPORT_SUBMITTED,
                        "주간 리포트 제출 알림",
                        "제출된 주간 리포트가 있어요. 피드백을 작성해 주세요."));
    }
}
