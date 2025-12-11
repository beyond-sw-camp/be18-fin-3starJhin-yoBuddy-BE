package com.j3s.yobuddy.domain.mentor.weeklyReport.scheduler;


import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.repository.WeeklyReportRepository;
import java.time.DayOfWeek;
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
public class WeeklyReportFeedbackOverdueScheduler {

    private final WeeklyReportRepository weeklyReportRepository;

    @Scheduled(cron = "0 0 1 * * SAT")
    @Transactional
    public void markFeedbackOverdueReports() {
        LocalDate today = LocalDate.now();
        log.info("[WeeklyReportFeedbackOverdueScheduler] Start - today={}", today);

        if (today.getDayOfWeek() != DayOfWeek.SATURDAY) {
            log.info("[WeeklyReportFeedbackOverdueScheduler] Skip - today is not Saturday");
            return;
        }

        LocalDate thisWeekMonday = today.with(DayOfWeek.MONDAY);

        List<WeeklyReport> candidates =
            weeklyReportRepository.findSubmittedReportsWithoutFeedbackBefore(thisWeekMonday);

        int updatedCount = 0;

        for (WeeklyReport report : candidates) {
            report.markFeedbackOverdue();
            updatedCount++;

            log.info(
                "[WeeklyReportFeedbackOverdueScheduler] Mark FEEDBACK_OVERDUE - weeklyReportId={}, menteeId={}, endDate={}",
                report.getWeeklyReportId(),
                report.getMenteeId(),
                report.getEndDate()
            );
        }

        log.info("[WeeklyReportFeedbackOverdueScheduler] End - updatedCount={}", updatedCount);
    }
}