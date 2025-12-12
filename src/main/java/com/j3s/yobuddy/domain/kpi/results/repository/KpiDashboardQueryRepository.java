package com.j3s.yobuddy.domain.kpi.results.repository;

import com.j3s.yobuddy.domain.kpi.results.dto.dashboard.DashboardOverviewResponse;
import java.time.LocalDate;

public interface KpiDashboardQueryRepository {

    DashboardOverviewResponse fetchOverviewByPeriod(LocalDate start, LocalDate end);
}