package com.j3s.yobuddy.domain.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RiskDistributionResponse {

    private int low;
    private int medium;
    private int high;
}
