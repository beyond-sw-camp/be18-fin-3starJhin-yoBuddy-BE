package com.j3s.yobuddy.domain.risk.dto;

import com.j3s.yobuddy.domain.risk.constant.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RiskScoreResult {

    private double score;
    private RiskLevel level;
}
