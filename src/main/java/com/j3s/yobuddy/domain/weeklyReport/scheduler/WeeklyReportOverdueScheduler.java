package com.j3s.yobuddy.domain.weeklyReport.scheduler;


import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.j3s.yobuddy.domain.weeklyReport.repository.WeeklyReportQueryRepository;
import com.j3s.yobuddy.domain.weeklyReport.repository.WeeklyReportRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyReportOverdueScheduler {

    private final WeeklyReportRepository weeklyReportRepository;

    /**
     * 매일 새벽 02:00 실행
     * "0 0 2 * * *"
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void markOverdueReports() {
        LocalDate today = LocalDate.now();

        log.info("[WeeklyReportOverdueScheduler] Start - today={}", today);

        // 🔍 QueryDSL로 DRAFT + endDate < today 조회
        List<WeeklyReport> candidates =
            weeklyReportRepository.findDraftReportsEndedBefore(today);

        for (WeeklyReport report : candidates) {
            report.changeStatus(WeeklyReportStatus.OVERDUE);
            log.info("[WeeklyReportOverdueScheduler] Marked OVERDUE - reportId={}, menteeId={}",
                report.getWeeklyReportId(), report.getMenteeId());
        }

        log.info("[WeeklyReportOverdueScheduler] End - updatedCount={}", candidates.size());
    }
}
