package com.j3s.yobuddy.domain.kpi.goals.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.kpi.goals.dto.request.KpiGoalsRequest;
import com.j3s.yobuddy.domain.kpi.goals.dto.response.KpiGoalsListResponse;
import com.j3s.yobuddy.domain.kpi.goals.dto.response.KpiGoalsResponse;
import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import com.j3s.yobuddy.domain.kpi.goals.exception.KpiGoalsAlreadyDeletedException;
import com.j3s.yobuddy.domain.kpi.goals.exception.KpiGoalsNotFoundException;
import com.j3s.yobuddy.domain.kpi.goals.repository.KpiGoalsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KpiGoalsServiceImpl implements KpiGoalsService {

    private final KpiGoalsRepository kpiGoalsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<KpiGoalsListResponse> getGoals(String description) {
        List<KpiGoals> result = (description == null || description.isBlank())
            ? kpiGoalsRepository.findAllByIsDeletedFalse()
            : kpiGoalsRepository.findByDescriptionContainingIgnoreCaseAndIsDeletedFalse(description);

        return result.stream().map(KpiGoalsListResponse::from).toList();
    }

    @Override
    @Transactional
    public void createGoal(KpiGoalsRequest request) {
        KpiGoals g = KpiGoals.builder()
            .programId(request.getProgramId())
            .description(request.getDescription())
            .targetValue(request.getTargetValue())
            .weight(request.getWeight())
            .kpiCategoryId(request.getKpiCategoryId())
            .departmentId(request.getDepartmentId())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isDeleted(false)
            .build();

        kpiGoalsRepository.save(g);
    }

    @Override
    @Transactional
    public KpiGoalsListResponse updateGoal(Long kpiGoalId, KpiGoalsRequest request) {
        KpiGoals g = kpiGoalsRepository.findByKpiGoalIdAndIsDeletedFalse(kpiGoalId)
            .orElseThrow(() -> new KpiGoalsNotFoundException(kpiGoalId));

        g.update(request.getProgramId(), request.getDescription(), request.getTargetValue(), request.getWeight(),
            request.getKpiCategoryId(), request.getDepartmentId());

        kpiGoalsRepository.save(g);

        return KpiGoalsListResponse.builder()
            .kpiGoalId(g.getKpiGoalId())
            .programId(g.getProgramId())
            .description(g.getDescription())
            .targetValue(g.getTargetValue())
            .weight(g.getWeight())
            .kpiCategoryId(g.getKpiCategoryId())
            .departmentId(g.getDepartmentId())
            .createdAt(g.getCreatedAt())
            .updatedAt(g.getUpdatedAt())
            .build();
    }

    @Override
    @Transactional
    public void deleteGoal(Long kpiGoalId) {
        KpiGoals g = kpiGoalsRepository.findByKpiGoalIdAndIsDeletedFalse(kpiGoalId)
            .orElseThrow(() -> new KpiGoalsNotFoundException(kpiGoalId));

        if (g.getIsDeleted()) {
            throw new KpiGoalsAlreadyDeletedException(kpiGoalId);
        }

        g.softDelete();
        kpiGoalsRepository.save(g);
    }

    @Override
    @Transactional(readOnly = true)
    public KpiGoalsResponse getGoalById(Long kpiGoalId) {
        KpiGoals g = kpiGoalsRepository.findById(kpiGoalId)
            .orElseThrow(() -> new KpiGoalsNotFoundException(kpiGoalId));

        return KpiGoalsResponse.from(g);
    }
}
