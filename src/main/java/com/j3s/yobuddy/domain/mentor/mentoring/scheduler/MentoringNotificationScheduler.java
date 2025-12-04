package com.j3s.yobuddy.domain.mentor.mentoring.scheduler;

import com.j3s.yobuddy.domain.mentor.mentoring.service.MentoringNotificationService;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentoringNotificationScheduler {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final MentoringNotificationService mentoringNotificationService;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void sendTomorrowSessions() {
        LocalDate today = LocalDate.now(ZONE);
        log.info("[MentoringNotificationScheduler] Triggered mentoring reminder for today={}", today);
        mentoringNotificationService.notifyTomorrowSessions(today);
        mentoringNotificationService.notifyMentorsForTodaySessions(today);
    }
}
