package com.j3s.yobuddy.domain.weeklyReport.scheduler;

import com.j3s.yobuddy.domain.weeklyReport.service.WeeklyReportMentorNotificationService;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyReportMentorNotificationScheduler {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final WeeklyReportMentorNotificationService weeklyReportMentorNotificationService;

    @Scheduled(cron = "0 0 10 * * FRI", zone = "Asia/Seoul")
    public void remindFeedbackDue() {
        LocalDate today = LocalDate.now(ZONE);
        log.info("[WeeklyReportMentorNotificationScheduler] Triggered feedback reminder for today={}", today);
        weeklyReportMentorNotificationService.notifyFeedbackPending(today);
    }
}
