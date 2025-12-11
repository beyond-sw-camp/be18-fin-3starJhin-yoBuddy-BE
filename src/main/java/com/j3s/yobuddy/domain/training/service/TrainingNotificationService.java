package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.training.entity.UserTraining;
import com.j3s.yobuddy.domain.training.repository.UserTrainingRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrainingNotificationService {

    private final UserTrainingRepository userTrainingRepository;
    private final NotificationService notificationService;

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    @Transactional(readOnly = true)
    public void notifyOnlineDueNextWeek(LocalDate today) {
        LocalDate target = today.plusWeeks(1);

        List<UserTraining> uts = userTrainingRepository.findOnlineDueAt(target);

        Map<User, Long> countByUser = uts.stream()
            .collect(Collectors.groupingBy(UserTraining::getUser, Collectors.counting()));

        countByUser.forEach((user, count) -> {
            notificationService.notify(
                user,
                NotificationType.ONLINE_DUE_NEXT_WEEK,
                "온라인 교육 수강 안내",
                "다음주까지 수강 완료해야 할 교육이 " + count + "개 있습니다."
            );
        });
    }

    @Transactional(readOnly = true)
    public void notifyOfflineNextWeek(LocalDate today) {
        LocalDate target = today.plusWeeks(1);

        List<UserTraining> uts = userTrainingRepository.findOfflineScheduledAt(target);

        Map<User, Long> countByUser = uts.stream()
            .collect(Collectors.groupingBy(UserTraining::getUser, Collectors.counting()));

        countByUser.forEach((user, count) -> {
            notificationService.notify(
                user,
                NotificationType.OFFLINE_NEXT_WEEK,
                "오프라인 교육 일정 안내",
                "다음주 예정되어 있는 오프라인 교육이 " + count + "개 있습니다."
            );
        });
    }

    @Transactional(readOnly = true)
    public void notifyOnlineDueTomorrow(LocalDate today) {
        LocalDate target = today.plusDays(1);

        List<UserTraining> uts = userTrainingRepository.findOnlineDueAt(target);

        Map<User, Long> countByUser = uts.stream()
            .collect(Collectors.groupingBy(UserTraining::getUser, Collectors.counting()));

        countByUser.forEach((user, count) -> {
            notificationService.notify(
                user,
                NotificationType.ONLINE_DUE_TOMORROW,
                "온라인 교육 마감 임박",
                "내일까지 수강 완료해야 할 교육이 " + count + "개 있습니다."
            );
        });
    }

    @Transactional(readOnly = true)
    public void notifyOfflineTomorrow(LocalDate today) {
        LocalDate target = today.plusDays(1);

        List<UserTraining> uts = userTrainingRepository.findOfflineScheduledAt(target);

        // 여기서 이미 UserRole.USER 로 필터된 상태 (Repository에서 필터했다는 전제)
        Map<User, Long> countByUser = uts.stream()
            .collect(Collectors.groupingBy(UserTraining::getUser, Collectors.counting()));

        countByUser.forEach((user, count) -> {
            notificationService.notify(
                user,
                NotificationType.OFFLINE_TOMORROW,
                "오프라인 교육 일정 안내",
                "내일 예정되어 있는 오프라인 교육이 " + count + "개 있습니다."
            );
        });
    }

    @Transactional(readOnly = true)
    public void notifyOfflineFormPending(LocalDate today) {
        LocalDate target = today.minusDays(1);

        List<UserTraining> uts = userTrainingRepository.findOfflineFormPendingAt(target);

        Map<User, Long> countByUser = uts.stream()
            .collect(Collectors.groupingBy(UserTraining::getUser, Collectors.counting()));

        countByUser.forEach((user, count) -> {
            notificationService.notify(
                user,
                NotificationType.OFFLINE_FORM_PENDING,
                "교육 설문 응시 안내",
                "응시할 구글 폼이 " + count + "개 있습니다."
            );
        });
    }
}
