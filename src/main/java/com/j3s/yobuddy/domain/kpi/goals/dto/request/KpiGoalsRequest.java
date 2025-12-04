package com.j3s.yobuddy.domain.kpi.goals.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KpiGoalsRequest {

    private final Long programId;
    private final String description;
    private final Integer targetValue;
    private final BigDecimal weight;
    private final Long kpiCategoryId;
    private final Long departmentId;

}

