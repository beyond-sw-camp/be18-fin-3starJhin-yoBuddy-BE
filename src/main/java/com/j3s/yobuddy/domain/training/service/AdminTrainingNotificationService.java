package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.repository.ProgramTrainingRepository;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminTrainingNotificationService {

    private final ProgramTrainingRepository programTrainingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public void notifyOfflineSchedule(LocalDate today, int daysAhead) {
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        List<ProgramTraining> items = programTrainingRepository
            .findByTraining_TypeAndScheduledAtBetweenAndProgram_DeletedFalse(
                TrainingType.OFFLINE,
                start,
                end
            );

        if (items.isEmpty()) {
            log.info("[AdminTrainingNotificationService] No offline trainings scheduled at targetDate={}", today);
            return;
        }

        NotificationType type = daysAhead == 7
            ? NotificationType.ADMIN_OFFLINE_NEXT_WEEK
            : NotificationType.ADMIN_OFFLINE_TOMORROW;

        String message = daysAhead == 7
            ? "일주일 뒤 예정되어있는 오프라인 교육이 있습니다."
            : "내일 예정되어 있는 오프라인 교육이 있습니다.";

        userRepository.findAllByIsDeletedFalse().stream()
            .filter(user -> user.getRole() == Role.ADMIN)
            .forEach(admin -> notificationService.notify(
                admin,
                type,
                "오프라인 교육 일정 알림",
                message
            ));

        log.info("[AdminTrainingNotificationService] Sent admin offline reminders (daysAhead={}) to admins", daysAhead);
    }
}
