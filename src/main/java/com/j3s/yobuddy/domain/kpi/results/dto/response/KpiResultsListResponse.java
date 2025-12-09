package com.j3s.yobuddy.domain.kpi.results.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.j3s.yobuddy.domain.kpi.results.entity.KpiResults;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KpiResultsListResponse {

    private final Long kpiResultId;
    private final BigDecimal achievedValue;
    private final BigDecimal score;
    private final LocalDateTime evaluatedAt;
    private final Long kpiGoalId;
    private final Long userId;
    private final Long departmentId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static KpiResultsListResponse from(KpiResults r) {
        return KpiResultsListResponse.builder()
            .kpiResultId(r.getKpiResultId())
            .achievedValue(r.getAchievedValue())
            .score(r.getScore())
            .evaluatedAt(r.getEvaluatedAt())
            .kpiGoalId(r.getKpiGoalId())
            .userId(r.getUserId())
            .departmentId(r.getDepartmentId())
            .createdAt(r.getCreatedAt())
            .updatedAt(r.getUpdatedAt())
            .build();
    }

}
