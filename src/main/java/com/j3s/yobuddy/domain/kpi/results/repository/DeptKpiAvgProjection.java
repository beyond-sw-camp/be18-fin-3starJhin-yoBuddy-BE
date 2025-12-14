package com.j3s.yobuddy.domain.kpi.results.repository;

import java.math.BigDecimal;

public interface DeptKpiAvgProjection {
    Long getKpiGoalId();
    BigDecimal getAvgScore();
}