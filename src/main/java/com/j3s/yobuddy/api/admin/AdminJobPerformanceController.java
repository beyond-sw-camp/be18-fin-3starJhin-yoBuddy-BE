package com.j3s.yobuddy.api.admin;

import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import com.j3s.yobuddy.domain.kpi.goals.repository.KpiGoalsRepository;
import com.j3s.yobuddy.domain.kpi.results.dto.request.JobPerformanceEvaluateRequest;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsResponse;
import com.j3s.yobuddy.domain.kpi.results.service.JobPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/kpi/job-performance")
public class AdminJobPerformanceController {

    private final JobPerformanceService service;
    private final KpiGoalsRepository kpiGoalsRepository;

    @PostMapping
    public ResponseEntity<Void> evaluate(
        @RequestBody JobPerformanceEvaluateRequest req
    ) {
        service.evaluate(null, req);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<KpiResultsResponse> getResult(
        @RequestParam Long userId,
        @RequestParam Long departmentId
    ) {
        KpiGoals goal = kpiGoalsRepository
            .findByDepartmentIdAndKpiCategoryIdAndIsDeletedFalse(
                departmentId,
                6L
            )
            .orElseThrow(() -> new IllegalStateException(
                "해당 부서의 직무수행능력 KPI Goal이 없습니다."
            ));

        return service
            .getJobPerformanceResult(userId, goal.getKpiGoalId())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.noContent().build());
    }
}
