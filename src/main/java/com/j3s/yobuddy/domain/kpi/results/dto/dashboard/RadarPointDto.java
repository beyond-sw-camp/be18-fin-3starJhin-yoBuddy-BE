package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RadarPointDto {
    private Long kpiGoalId;
    private String label;
    private BigDecimal avgScore;
}
