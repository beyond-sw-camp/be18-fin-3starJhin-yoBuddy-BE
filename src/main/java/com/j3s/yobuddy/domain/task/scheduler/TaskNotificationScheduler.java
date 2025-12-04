package com.j3s.yobuddy.domain.task.scheduler;

import com.j3s.yobuddy.domain.task.service.TaskNotificationService;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskNotificationScheduler {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final TaskNotificationService taskNotificationService;

    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
    public void runDailyTaskReminders() {
        LocalDate today = LocalDate.now(ZONE);
        log.info("[TaskNotificationScheduler] Triggered task reminders for today={}", today);
        taskNotificationService.notifyDueInDays(today, 7);
        taskNotificationService.notifyDueInDays(today, 1);
    }
}
