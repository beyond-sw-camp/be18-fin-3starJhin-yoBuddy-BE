package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KpiGoalDto {
    private Long kpiGoalId;
    private String description;
    private Integer targetValue;
    private BigDecimal weight;
    private Long kpiCategoryId;
    private Long departmentId;

    public static KpiGoalDto from(KpiGoals g) {
        return KpiGoalDto.builder()
            .kpiGoalId(g.getKpiGoalId())
            .description(g.getDescription())
            .targetValue(g.getTargetValue())
            .weight(g.getWeight())
            .kpiCategoryId(g.getKpiCategoryId())
            .departmentId(g.getDepartmentId())
            .build();
    }
}