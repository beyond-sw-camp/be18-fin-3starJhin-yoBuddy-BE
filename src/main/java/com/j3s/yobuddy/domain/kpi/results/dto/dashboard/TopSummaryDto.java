package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopSummaryDto {

    private String periodLabel;        // "2025 상반기"
    private LocalDate startDate;       // 온보딩 시작일
    private LocalDate endDate;         // 온보딩 종료일

    private long newUserCount;         // 신입 수
    private long totalMentoringCount;  // 총 멘토링 횟수
    private BigDecimal avgTaskScore;   // 평균 과제 점수
    private BigDecimal avgKpiScore;    // KPI 종합 점수
}
