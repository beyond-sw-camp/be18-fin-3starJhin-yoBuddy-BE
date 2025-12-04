package com.j3s.yobuddy.domain.weeklyReport.service;


import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport;
import com.j3s.yobuddy.domain.weeklyReport.entity.WeeklyReport.WeeklyReportStatus;
import com.j3s.yobuddy.domain.weeklyReport.repository.WeeklyReportRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyReportNotificationServiceImpl implements WeeklyReportNotificationService {

    private final WeeklyReportRepository weeklyReportRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Override
    public void notifyDraftsDueToday(LocalDate today) {

        // 1) 오늘 마감이고, 상태가 DRAFT인 주간 리포트 조회
        List<WeeklyReport> dueReports =
            weeklyReportRepository.findByEndDateAndStatus(today, WeeklyReportStatus.DRAFT);

        log.info("[WeeklyReportNotificationService] Found {} draft reports due today={}",
            dueReports.size(), today);

        // 2) 각 멘티에게 알림 발송
        for (WeeklyReport report : dueReports) {
            User mentee = userRepository.findById(report.getMenteeId())
                .orElse(null);

            notificationService.notify(
                mentee,
                NotificationType.WEEKLY_REPORT_DUE_TODAY,
                "주간 리포트 제출 마감",
                "오늘 제출해야 하는 주간 리포트가 있어요."
            );
        }
    }
}
