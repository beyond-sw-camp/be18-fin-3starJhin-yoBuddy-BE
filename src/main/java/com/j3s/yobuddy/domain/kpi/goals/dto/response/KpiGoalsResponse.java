package com.j3s.yobuddy.domain.kpi.goals.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KpiGoalsResponse {

    private Long kpiGoalId;
    private Long programId;
    private String description;
    private Integer targetValue;
    private BigDecimal weight;
    private Long kpiCategoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static KpiGoalsResponse from(KpiGoals g) {
        return KpiGoalsResponse.builder()
            .kpiGoalId(g.getKpiGoalId())
            .programId(g.getProgramId())
            .description(g.getDescription())
            .targetValue(g.getTargetValue())
            .weight(g.getWeight())
            .kpiCategoryId(g.getKpiCategoryId())
            .createdAt(g.getCreatedAt())
            .updatedAt(g.getUpdatedAt())
            .build();
    }
}
