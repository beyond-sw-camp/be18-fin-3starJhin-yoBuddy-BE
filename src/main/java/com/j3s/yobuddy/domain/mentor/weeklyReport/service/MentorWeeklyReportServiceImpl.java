package com.j3s.yobuddy.domain.mentor.weeklyReport.service;

import com.j3s.yobuddy.domain.mentor.menteeAssignment.repository.MentorMenteeAssignmentRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.request.MentorWeeklyReportFeedbackRequest;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response.MentorWeeklyReportDetailResponse;
import com.j3s.yobuddy.domain.mentor.weeklyReport.dto.response.MentorWeeklyReportSummaryResponse;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.j3s.yobuddy.domain.mentor.weeklyReport.exception.MentorWeeklyReportAccessDeniedException;
import com.j3s.yobuddy.domain.mentor.weeklyReport.exception.WeeklyReportFeedbackNotAllowedException;
import com.j3s.yobuddy.domain.weeklyReport.exception.WeeklyReportNotFoundException;
import com.j3s.yobuddy.domain.weeklyReport.repository.WeeklyReportRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentorWeeklyReportServiceImpl implements MentorWeeklyReportService {

    private final WeeklyReportRepository weeklyReportRepository;
    private final MentorMenteeAssignmentRepository mentorMenteeAssignmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<MentorWeeklyReportSummaryResponse> getMenteeWeeklyReports(Long mentorId,
        Long menteeId,
        WeeklyReportStatus status,
        Integer weekNumber,
        Pageable pageable) {

        boolean assigned = mentorMenteeAssignmentRepository
            .existsByMentorUserIdAndMenteeUserIdAndDeletedFalse(mentorId, menteeId);

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

        String menteeName = userRepository.findById(menteeId)
            .map(User::getName)
            .orElse(null);

        return MentorWeeklyReportDetailResponse.from(report, menteeName);
    }
}
