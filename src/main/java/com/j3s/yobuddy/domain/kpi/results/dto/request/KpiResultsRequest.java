package com.j3s.yobuddy.domain.kpi.results.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KpiResultsRequest {

    private final BigDecimal achievedValue;
    private final BigDecimal score;
    private final LocalDateTime evaluatedAt;
    private final Long kpiGoalId;
    private final Long userId;
    private final Long departmentId;

}

