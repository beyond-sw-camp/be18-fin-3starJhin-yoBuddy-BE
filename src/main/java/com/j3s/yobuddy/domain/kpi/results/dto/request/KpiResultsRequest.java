package com.j3s.yobuddy.domain.kpi.results.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KpiResultsRequest {

    private BigDecimal achievedValue;
    private BigDecimal score;
    private LocalDateTime evaluatedAt;
    private Long kpiGoalId;
    private Long userId;
    private Long departmentId;

}
