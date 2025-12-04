package com.j3s.yobuddy.domain.weeklyReport.service;

import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.repository.WeeklyReportRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeeklyReportMentorNotificationService {

    private final WeeklyReportRepository weeklyReportRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * Notify mentors to provide feedback for last week's submitted reports (no feedback yet).
     */
    @Transactional(readOnly = true)
    public void notifyFeedbackPending(LocalDate today) {
        LocalDate thisWeekMonday = today.with(DayOfWeek.MONDAY);
        LocalDate lastWeekMonday = thisWeekMonday.minusWeeks(1);

        var reports = weeklyReportRepository.findSubmittedReportsWithoutFeedbackBetween(
            lastWeekMonday,
            thisWeekMonday
        );

        if (reports.isEmpty()) {
            log.info("[WeeklyReportMentorNotificationService] No pending feedback reports for last week.");
            return;
        }

        Set<Long> mentorIds = reports.stream()
            .map(WeeklyReport::getMentorId)
            .collect(Collectors.toSet());

        Map<Long, User> mentors = userRepository.findAllById(mentorIds).stream()
            .filter(user -> !user.isDeleted() && user.getRole() == Role.MENTOR)
            .collect(Collectors.toMap(User::getUserId, Function.identity()));

        mentors.values().forEach(mentor -> notificationService.notify(
            mentor,
            NotificationType.MENTOR_WEEKLY_REPORT_FEEDBACK_DUE,
            "주간 리포트 피드백 알림",
            "오늘까지 피드백을 작성해야 할 주간 리포트가 있어요."
        ));

        log.info("[WeeklyReportMentorNotificationService] Sent feedback reminder to {} mentors", mentors.size());
    }
}
