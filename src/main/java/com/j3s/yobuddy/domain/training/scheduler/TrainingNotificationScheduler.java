package com.j3s.yobuddy.domain.training.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.j3s.yobuddy.domain.training.service.TrainingNotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TrainingNotificationScheduler {

    private final TrainingNotificationService trainingNotificationService;
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void runDailyNotifications() {
        LocalDate today = LocalDate.now(ZONE);

        trainingNotificationService.notifyOnlineDueNextWeek(today);
        trainingNotificationService.notifyOfflineNextWeek(today);
        trainingNotificationService.notifyOnlineDueTomorrow(today);
        trainingNotificationService.notifyOfflineTomorrow(today);
        trainingNotificationService.notifyOfflineFormPending(today);
    }
}
