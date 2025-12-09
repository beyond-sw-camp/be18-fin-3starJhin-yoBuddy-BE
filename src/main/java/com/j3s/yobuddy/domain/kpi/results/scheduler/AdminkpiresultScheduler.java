package com.j3s.yobuddy.domain.kpi.results.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import com.j3s.yobuddy.domain.kpi.results.service.KpiResultsService;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import com.j3s.yobuddy.domain.kpi.goals.repository.KpiGoalsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminkpiresultScheduler {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final KpiResultsService kpiResultsService;
    private final UserRepository userRepository;
    private final KpiGoalsRepository kpiGoalsRepository;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void kpiresultSchedules() {
        LocalDate today = LocalDate.now(ZONE);

        // Only run on the last day of the month
        if (today.getDayOfMonth() != today.lengthOfMonth()) {
            log.debug("Today is {} — not last day of month ({}). Skipping KPI schedule.", today, today.lengthOfMonth());
            return;
        }
        List<User> users = userRepository.findAllByIsDeletedFalse();
        int usersProcessed = 0;
        int goalsCreated = 0;
        int usersSkipped = 0;

        for (User user : users) {
            try {
                if (user == null || user.getDepartment() == null) {
                    usersSkipped++;
                    log.debug("Skipping user with missing department: {}", user == null ? "null" : user.getUserId());
                    continue;
                }

                Long userId = user.getUserId();
                Long departmentId = user.getDepartment().getDepartmentId();

                List<KpiGoals> kpiGoals = kpiGoalsRepository.findByDepartmentIdAndIsDeletedFalse(departmentId);
                if (kpiGoals == null || kpiGoals.isEmpty()) {
                    log.debug("No KPI goals found for department {} (user {}).", departmentId, userId);
                    usersProcessed++;
                    continue;
                }

                for (KpiGoals kpiGoal : kpiGoals) {
                    try {
                        kpiResultsService.createResult(userId, departmentId, kpiGoal);
                        goalsCreated++;
                    } catch (Exception e) {
                        log.warn("Failed to create KPI result for user {} and goal {}: {}", userId,
                            kpiGoal == null ? null : kpiGoal.getKpiGoalId(), e.getMessage());
                    }
                }

                usersProcessed++;
            } catch (Exception ex) {
                usersSkipped++;
                log.error("Unexpected error processing user {}: {}", user == null ? "null" : user.getUserId(), ex.getMessage(), ex);
            }
        }

        log.info("KPI scheduler summary for {}: usersProcessed={}, usersSkipped={}, goalsCreated={}", today, usersProcessed, usersSkipped, goalsCreated);
    }
}