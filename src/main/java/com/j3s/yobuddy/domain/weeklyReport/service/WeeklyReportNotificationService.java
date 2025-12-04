package com.j3s.yobuddy.domain.weeklyReport.service;

import java.time.LocalDate;

public interface WeeklyReportNotificationService {

    void notifyDraftsDueToday(LocalDate today);
}
