package com.j3s.yobuddy.domain.training.scheduler;

import com.j3s.yobuddy.domain.training.service.AdminTrainingNotificationService;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminTrainingNotificationScheduler {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final AdminTrainingNotificationService adminTrainingNotificationService;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void sendOfflineSchedules() {
        LocalDate today = LocalDate.now(ZONE);
        log.info("[AdminTrainingNotificationScheduler] Triggered for today={}", today);
        adminTrainingNotificationService.notifyOfflineSchedule(today, 7);
        adminTrainingNotificationService.notifyOfflineSchedule(today, 1);
    }
}
