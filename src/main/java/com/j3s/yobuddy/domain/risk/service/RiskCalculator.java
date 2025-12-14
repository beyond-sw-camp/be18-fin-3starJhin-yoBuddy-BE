package com.j3s.yobuddy.domain.risk.service;

import com.j3s.yobuddy.domain.risk.constant.RiskLevel;
import com.j3s.yobuddy.domain.risk.dto.RiskScoreResult;
import org.springframework.stereotype.Component;

@Component
public class RiskCalculator {

    public RiskScoreResult calculate(
        double taskDelayRate,
        double mentoringAbsentRate,
        double weeklyReportMissingRate,
        double kpiDeviation
    ) {

        double score =
            taskDelayRate * 0.3
                + mentoringAbsentRate * 0.2
                + weeklyReportMissingRate * 0.3
                + kpiDeviation * 0.2;

        RiskLevel level;
        if (score <= 30) {
            level = RiskLevel.LOW;
        } else if (score <= 70) {
            level = RiskLevel.MEDIUM;
        } else {
            level = RiskLevel.HIGH;
        }

        return new RiskScoreResult(score, level);
    }
}
