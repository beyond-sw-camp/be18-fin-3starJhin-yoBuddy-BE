package com.j3s.yobuddy.api.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.j3s.yobuddy.domain.kpi.results.dto.request.KpiResultsRequest;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsListResponse;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsResponse;
import com.j3s.yobuddy.domain.kpi.results.service.KpiResultsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/kpi/results")
public class AdminKpiResultsController {

    private final KpiResultsService kpiResultsService;

    @GetMapping
    public ResponseEntity<List<KpiResultsListResponse>> getResults(
        @RequestParam(required = false) Long kpiGoalId,
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) Long departmentId) {

        List<KpiResultsListResponse> list = kpiResultsService.getResults(kpiGoalId, userId, departmentId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{kpiResultId}")
    public ResponseEntity<KpiResultsResponse> getResultById(@PathVariable("kpiResultId") Long kpiResultId) {
        KpiResultsResponse resp = kpiResultsService.getResultById(kpiResultId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<String> createResult(@RequestBody KpiResultsRequest request) {
        kpiResultsService.createResult(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("KPI 결과가 생성되었습니다.");
    }

    // optional endpoints for completeness
    @PatchMapping("/{kpiResultId}")
    public ResponseEntity<String> updateResult(@PathVariable("kpiResultId") Long kpiResultId,
        @RequestBody KpiResultsRequest request) {
        kpiResultsService.updateResult(kpiResultId, request);
        return ResponseEntity.ok("KPI 결과가 수정되었습니다.");
    }

    @DeleteMapping("/{kpiResultId}")
    public ResponseEntity<String> deleteResult(@PathVariable("kpiResultId") Long kpiResultId) {
        // Not implemented: could soft-delete via service
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("삭제는 추후 구현됩니다.");
    }
}
