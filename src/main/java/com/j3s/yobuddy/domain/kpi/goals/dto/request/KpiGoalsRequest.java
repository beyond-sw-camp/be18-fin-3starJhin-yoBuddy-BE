package com.j3s.yobuddy.domain.kpi.goals.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KpiGoalsRequest {

    private Long programId;
    private String description;
    private Integer targetValue;
    private BigDecimal weight;
    private Long kpiCategoryId;

}
