package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.entity.UserTaskStatus;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
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
public class TaskNotificationService {

    private final UserTaskRepository userTaskRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public void notifyDueInDays(LocalDate today, int daysAhead) {
        LocalDate targetDate = today.plusDays(daysAhead);
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(LocalTime.MAX);

        List<UserTask> tasks = userTaskRepository.findByDeletedFalseAndStatusInAndProgramTask_DueDateBetween(
            EnumSet.of(UserTaskStatus.PENDING, UserTaskStatus.LATE).stream().toList(),
            start,
            end
        );

        if (tasks.isEmpty()) {
            log.info("[TaskNotificationService] No tasks due in {} days (targetDate={})", daysAhead, targetDate);
            return;
        }

        Map<User, Long> countByUser = tasks.stream()
            .map(UserTask::getUser)
            .filter(user -> !user.isDeleted() && user.getRole() == Role.USER)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        NotificationType type = daysAhead == 7
            ? NotificationType.TASK_DUE_NEXT_WEEK
            : NotificationType.TASK_DUE_TOMORROW;

        countByUser.forEach((user, count) -> notificationService.notify(
            user,
            type,
            "과제 마감 알림",
            buildMessage(daysAhead, count)
        ));

        log.info(
            "[TaskNotificationService] Sent task due reminder (daysAhead={}) to {} users",
            daysAhead,
            countByUser.size()
        );
    }

    private String buildMessage(int daysAhead, Long count) {
        if (daysAhead == 7) {
            return "일주일 뒤 마감인 과제가 " + count + " 개 있어요.";
        }
        return "내일 마감인 과제가 " + count + " 개 있어요.";
    }
}
