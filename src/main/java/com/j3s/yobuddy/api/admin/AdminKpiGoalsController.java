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

import com.j3s.yobuddy.domain.kpi.goals.dto.request.KpiGoalsRequest;
import com.j3s.yobuddy.domain.kpi.goals.dto.response.KpiGoalsListResponse;
import com.j3s.yobuddy.domain.kpi.goals.dto.response.KpiGoalsResponse;
import com.j3s.yobuddy.domain.kpi.goals.service.KpiGoalsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin/kpi/goals")
public class AdminKpiGoalsController {

    private final KpiGoalsService kpiGoalsService;

    @GetMapping
    public ResponseEntity<List<KpiGoalsListResponse>> getGoals(
        @RequestParam(required = false) String description) {

        List<KpiGoalsListResponse> list = kpiGoalsService.getGoals(description);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{kpiGoalId}")
    public ResponseEntity<KpiGoalsResponse> getGoalById(@PathVariable("kpiGoalId") Long kpiGoalId) {
        KpiGoalsResponse resp = kpiGoalsService.getGoalById(kpiGoalId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<String> createGoal(@RequestBody KpiGoalsRequest request) {
        if (request.getKpiCategoryId() == null) {
            return ResponseEntity.badRequest().body("kpiCategoryId must not be null");
        }

        kpiGoalsService.createGoal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("KPI 목표가 생성되었습니다.");
    }

    @PatchMapping("/{kpiGoalId}")
    public ResponseEntity<String> updateGoal(@PathVariable("kpiGoalId") Long kpiGoalId,
        @RequestBody KpiGoalsRequest request) {

        if (request.getKpiCategoryId() == null) {
            return ResponseEntity.badRequest().body("kpiCategoryId must not be null");
        }

        kpiGoalsService.updateGoal(kpiGoalId, request);
        return ResponseEntity.ok("KPI 목표가 수정되었습니다.");
    }

    @DeleteMapping("/{kpiGoalId}")
    public ResponseEntity<String> deleteGoal(@PathVariable("kpiGoalId") Long kpiGoalId) {
        kpiGoalsService.deleteGoal(kpiGoalId);
        return ResponseEntity.ok("KPI 목표가 삭제되었습니다.");
    }
}
