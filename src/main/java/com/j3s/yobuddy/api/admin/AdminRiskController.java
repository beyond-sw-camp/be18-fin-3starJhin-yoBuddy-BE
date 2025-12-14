package com.j3s.yobuddy.api.admin;

import com.j3s.yobuddy.domain.risk.dto.RiskDistributionResponse;
import com.j3s.yobuddy.domain.risk.service.RiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/programs")
public class AdminRiskController {

    private final RiskService riskService;

    @GetMapping("/{programId}/risk-distribution")
    public RiskDistributionResponse getRiskDistribution(
        @PathVariable Long programId
    ) {
        return riskService.getRiskDistribution(programId);
    }
}
