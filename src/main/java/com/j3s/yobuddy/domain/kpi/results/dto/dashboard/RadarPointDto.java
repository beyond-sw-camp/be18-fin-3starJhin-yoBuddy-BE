package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Getter;

@Getter
public class RadarPointDto {

    private Long kpiGoalId;
    private String label;
    private BigDecimal avgScore;

    public RadarPointDto(Long kpiGoalId, String label, BigDecimal avgScore) {
        this.kpiGoalId = kpiGoalId;
        this.label = label;
        this.avgScore = avgScore == null
            ? BigDecimal.ZERO
            : avgScore.setScale(2, RoundingMode.HALF_UP);
    }

    public RadarPointDto(Long kpiGoalId, String label, Double avgScore) {
        this.kpiGoalId = kpiGoalId;
        this.label = label;
        this.avgScore = avgScore == null
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(avgScore).setScale(2, RoundingMode.HALF_UP);
    }
}
