package com.j3s.yobuddy.domain.mentor.weeklyReport.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.mentor.menteeAssignment.repository.MentorMenteeAssignmentRepository;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.request.MentorWeeklyReportFeedbackRequest;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response.MentorWeeklyReportDetailResponse;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response.MentorWeeklyReportSummaryResponse;
import com.j3s.yobuddy.domain.mentor.weeklyReport.exception.MentorWeeklyReportAccessDeniedException;
import com.j3s.yobuddy.domain.mentor.weeklyReport.exception.WeeklyReportFeedbackNotAllowedException;
import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.j3s.yobuddy.domain.weeklyReport.exception.WeeklyReportNotFoundException;
import com.j3s.yobuddy.domain.weeklyReport.repository.WeeklyReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MentorWeeklyReportServiceImpl implements MentorWeeklyReportService {

    private final WeeklyReportRepository weeklyReportRepository;
    private final MentorMenteeAssignmentRepository mentorMenteeAssignmentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public Page<MentorWeeklyReportSummaryResponse> getMenteeWeeklyReports(Long mentorId,
        Long menteeId,
        WeeklyReportStatus status,
        Integer weekNumber,
        Pageable pageable) {

        boolean assigned = mentorMenteeAssignmentRepository
            .existsByMentorUserIdAndMenteeUserId(mentorId, menteeId);

        if (!assigned) {
            throw new MentorWeeklyReportAccessDeniedException(mentorId, menteeId);
        }

        Page<WeeklyReport> page = weeklyReportRepository
            .findWeeklyReportsForMentor(mentorId, menteeId, status, weekNumber, pageable);

        Optional<User> menteeOpt = userRepository.findById(menteeId);
        String menteeName = menteeOpt.map(User::getName).orElse(null);

        return page.map(report -> MentorWeeklyReportSummaryResponse.from(report, menteeName));
    }

    @Override
    @Transactional(readOnly = true)
    public MentorWeeklyReportDetailResponse getWeeklyReportDetail(Long mentorId,
        Long menteeId,
        Long weeklyReportId) {

        WeeklyReport report = weeklyReportRepository.findById(weeklyReportId)
            .orElseThrow(() -> new WeeklyReportNotFoundException(weeklyReportId));

        if (!mentorId.equals(report.getMentorId()) || !menteeId.equals(report.getMenteeId())) {
            throw new MentorWeeklyReportAccessDeniedException(mentorId, menteeId);
        }

        String menteeName = userRepository.findById(menteeId)
            .map(User::getName)
            .orElse(null);

        return MentorWeeklyReportDetailResponse.from(report, menteeName);
    }

    @Override
    @Transactional
    public MentorWeeklyReportDetailResponse submitFeedback(Long mentorId,
        Long menteeId,
        Long weeklyReportId,
        MentorWeeklyReportFeedbackRequest request) {

        WeeklyReport report = weeklyReportRepository.findById(weeklyReportId)
            .orElseThrow(() -> new WeeklyReportNotFoundException(weeklyReportId));

        if (!mentorId.equals(report.getMentorId()) || !menteeId.equals(report.getMenteeId())) {
            throw new MentorWeeklyReportAccessDeniedException(mentorId, menteeId);
        }

        if (report.getStatus() != WeeklyReportStatus.SUBMITTED) {
            throw new WeeklyReportFeedbackNotAllowedException();
        }

        WeeklyReportStatus newStatus = WeeklyReportStatus.REVIEWED;
        if (request.getStatus() != null) {
            newStatus = WeeklyReportStatus.valueOf(request.getStatus());
        }

        report.updateMentorFeedback(request.getMentorFeedback(), newStatus);

        User mentee = userRepository.findById(menteeId).orElse(null);

        if (mentee != null && !mentee.isDeleted() && mentee.getRole() == Role.USER) {
            notificationService.notify(
                mentee,
                NotificationType.WEEKLY_REPORT_FEEDBACK,
                "주간 리포트 피드백 알림",
                "주간리포트에 피드백이 등록되었어요."
            );
        }

        String menteeName = mentee != null ? mentee.getName() : null;

        return MentorWeeklyReportDetailResponse.from(report, menteeName);
    }
    @Override
    @Transactional(readOnly = true)
    public List<MentorWeeklyReportDetailResponse> getWeeklyReportsByUserId(String userId) {
        try {
            Long menteeId = Long.parseLong(userId);

            String menteeName = userRepository.findByUserIdAndIsDeletedFalse(menteeId)
                .map(User::getName)
                .orElse(null);

            var page = weeklyReportRepository.findByMenteeId(menteeId, Pageable.unpaged());
            List<MentorWeeklyReportDetailResponse> result = new ArrayList<>();
            for (WeeklyReport report : page.getContent()) {
                result.add(MentorWeeklyReportDetailResponse.from(report, menteeName));
            }
            return result;
        } catch (NumberFormatException ex) {
            return new ArrayList<>();
        }
    }
}
