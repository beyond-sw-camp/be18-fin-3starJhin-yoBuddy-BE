// file: src/main/java/com/j3s/yobuddy/domain/kpi/results/scheduler/AdminkpiresultScheduler.java
package com.j3s.yobuddy.domain.kpi.results.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.j3s.yobuddy.domain.kpi.results.service.KpiResultsService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminkpiresultScheduler {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private final KpiResultsService kpiResultsService;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void kpiresultSchedules() {
        LocalDate today = LocalDate.now(ZONE);

        // last day of month만 실행
        if (today.getDayOfMonth() != today.lengthOfMonth()) {
            log.debug("Today is {} — not last day of month. Skipping KPI schedule.", today);
            return;
        }

        // ✅ 운영 스케줄은 보통 "현재 진행중 프로그램"만
        kpiResultsService.calculateKpiResults(false, false);

        log.info("KPI scheduler executed at {}", today);
    }
}
