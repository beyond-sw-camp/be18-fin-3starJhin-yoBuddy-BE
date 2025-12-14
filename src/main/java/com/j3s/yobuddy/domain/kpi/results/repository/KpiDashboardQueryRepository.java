package com.j3s.yobuddy.domain.kpi.results.repository;

import com.j3s.yobuddy.domain.kpi.results.dto.dashboard.DashboardOverviewResponse;
import com.j3s.yobuddy.domain.kpi.results.dto.dashboard.UserKpiDetailResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface KpiDashboardQueryRepository {

    /* =========================
     *  PERIOD DASHBOARD
     * ========================= */
    DashboardOverviewResponse fetchOverviewByPeriod(
        LocalDate start,
        LocalDate end
    );

    /* =========================
     *  RADAR
     * ========================= */
    List<UserKpiDetailResponse.RadarItem> fetchUserGoalRadar(
        Long userId,
        Long departmentId,
        Long programId
    );

    List<UserKpiDetailResponse.RadarItem> fetchDeptGoalRadar(
        Long departmentId,
        Long programId
    );

    /* =========================
     *  PROGRESS
     * ========================= */
    UserKpiDetailResponse.ProgressSummary fetchUserTaskProgress(
        Long userId,
        Long programId
    );

    UserKpiDetailResponse.ProgressSummary fetchUserEducationProgress(
        Long userId,
        Long programId
    );

    /* =========================
     *  SCORE / COUNT
     * ========================= */
    BigDecimal fetchUserAvgTaskScore(
        Long userId,
        Long programId
    );

    long fetchUserMentoringCompletedCount(
        Long userId,
        Long programId
    );

    /* =========================
     *  PROGRAM RESOLUTION
     * ========================= */
    Long fetchLatestProgramIdForUser(
        Long userId,
        Long departmentId
    );
}
