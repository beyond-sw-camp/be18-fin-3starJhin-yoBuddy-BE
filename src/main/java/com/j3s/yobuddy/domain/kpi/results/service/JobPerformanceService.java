package com.j3s.yobuddy.domain.kpi.results.service;

import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import com.j3s.yobuddy.domain.kpi.goals.repository.KpiGoalsRepository;
import com.j3s.yobuddy.domain.kpi.results.dto.request.JobPerformanceEvaluateRequest;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsResponse;
import com.j3s.yobuddy.domain.kpi.results.entity.KpiResults;
import com.j3s.yobuddy.domain.kpi.results.repository.KpiResultsRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobPerformanceService {

    private final KpiResultsRepository kpiResultsRepository;
    private final KpiGoalsRepository kpiGoalsRepository;

    @Transactional
    public void evaluate(
        Long evaluatorId,
        JobPerformanceEvaluateRequest req
    ) {
        int score = req.totalScore();

        if (req.getDepartmentId() == null) {
            throw new IllegalArgumentException("부서가 있어야 합니다.");
        }

        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("직무 수행 능력 점수는 0~100");
        }

        KpiGoals goal = kpiGoalsRepository
            .findByDepartmentIdAndKpiCategoryIdAndIsDeletedFalse(
                req.getDepartmentId(),
                6L   // 직무수행능력 카테고리 ID
            )
            .orElseThrow(() -> new IllegalStateException(
                "해당 부서의 직무수행능력 KPI Goal이 없습니다."
            ));

        boolean exists =
            kpiResultsRepository
                .findTopByUserIdAndKpiGoalIdAndIsDeletedFalseOrderByEvaluatedAtDesc(
                    req.getUserId(),
                    goal.getKpiGoalId()
                )
                .isPresent();

        if (exists) {
            throw new IllegalStateException("이미 직무 수행 능력 평가가 완료된 사용자입니다.");
        }

        KpiResults result = KpiResults.builder()
            .userId(req.getUserId())
            .departmentId(req.getDepartmentId())
            .kpiGoalId(goal.getKpiGoalId())
            .achievedValue(BigDecimal.valueOf(score))
            .evaluatedAt(LocalDateTime.now())
            .isDeleted(false)
            .build();

        kpiResultsRepository.save(result);
    }

    @Transactional(readOnly = true)
    public Optional<KpiResultsResponse> getJobPerformanceResult(
        Long userId,
        Long kpiGoalId
    ) {
        return kpiResultsRepository
            .findTopByUserIdAndKpiGoalIdAndIsDeletedFalseOrderByEvaluatedAtDesc(
                userId, kpiGoalId
            )
            .map(KpiResultsResponse::from);
    }
}
