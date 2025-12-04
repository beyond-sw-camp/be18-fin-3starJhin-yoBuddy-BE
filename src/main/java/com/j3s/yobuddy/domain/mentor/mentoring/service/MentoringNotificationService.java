package com.j3s.yobuddy.domain.mentor.mentoring.service;

import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringSession;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;
import com.j3s.yobuddy.domain.mentor.mentoring.repository.MentoringSessionRepository;
import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentoringNotificationService {

    private final MentoringSessionRepository mentoringSessionRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public void notifyTomorrowSessions(LocalDate today) {
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        var sessions = mentoringSessionRepository
            .findByScheduledAtBetweenAndDeletedFalseAndStatus(
                start,
                end,
                MentoringStatus.SCHEDULED
            );

        if (sessions.isEmpty()) {
            log.info("[MentoringNotificationService] No sessions scheduled for tomorrow={}", today);
            return;
        }

        Map<User, Long> mentees = sessions.stream()
            .map(MentoringSession::getMentee)
            .filter(user -> !user.isDeleted() && user.getRole() == Role.USER)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        mentees.keySet().forEach(user -> notificationService.notify(
            user,
            NotificationType.MENTORING_SESSION_TODAY,
            "멘토링 세션 알림",
            "오늘 예정되어 있는 멘토링 세션이 있어요."
        ));

        log.info("[MentoringNotificationService] Sent mentoring reminders to {} users", mentees.size());
    }

    @Transactional(readOnly = true)
    public void notifyMentorsForTodaySessions(LocalDate today) {
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        var sessions = mentoringSessionRepository
            .findByScheduledAtBetweenAndDeletedFalseAndStatus(
                start,
                end,
                MentoringStatus.SCHEDULED
            );

        if (sessions.isEmpty()) {
            log.info("[MentoringNotificationService] No sessions scheduled today={}", today);
            return;
        }

        Map<User, Long> mentors = sessions.stream()
            .map(MentoringSession::getMentor)
            .filter(user -> !user.isDeleted() && user.getRole() == Role.MENTOR)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        mentors.keySet().forEach(mentor -> notificationService.notify(
            mentor,
            NotificationType.MENTOR_MENTORING_TODAY,
            "멘토링 세션 피드백 알림",
            "멘토링 진행 후 피드백을 작성해 주세요."
        ));

        log.info("[MentoringNotificationService] Sent mentor feedback reminders to {} mentors", mentors.size());
    }
}
