package com.j3s.yobuddy.api.admin;

import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiCompareResponse;
import com.j3s.yobuddy.domain.kpi.results.service.KpiCompareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/kpi")
public class AdminKpiCompareController {

    private final KpiCompareService service;

    @GetMapping("/compare")
    public ResponseEntity<KpiCompareResponse> compare(
        @RequestParam Long userId
    ) {
        return ResponseEntity.ok(
            service.compare(userId)
        );
    }
}