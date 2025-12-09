package com.j3s.yobuddy.domain.kpi.results.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsListResponse;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsResponse;
import com.j3s.yobuddy.domain.kpi.results.entity.KpiResults;
import com.j3s.yobuddy.domain.kpi.results.exception.KpiResultsNotFoundException;
import com.j3s.yobuddy.domain.kpi.results.repository.KpiResultsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KpiResultsServiceImpl implements KpiResultsService {

    private final KpiResultsRepository kpiResultsRepository;
    private final KpiScoreCalculator kpiScoreCalculator;

    @Override
    @Transactional(readOnly = true)
    public List<KpiResultsListResponse> getResults(Long kpiGoalId, Long userId, Long departmentId) {
        List<KpiResults> result;
        if (kpiGoalId != null) {
            result = kpiResultsRepository.findByKpiGoalIdAndIsDeletedFalse(kpiGoalId);
        } else if (userId != null) {
            result = kpiResultsRepository.findByUserIdAndIsDeletedFalse(userId);
        } else if (departmentId != null) {
            result = kpiResultsRepository.findByDepartmentIdAndIsDeletedFalse(departmentId);
        } else {
            result = kpiResultsRepository.findAllByIsDeletedFalse();
        }

        return result.stream().map(KpiResultsListResponse::from).toList();
    }

    @Override
    @Transactional
    public void createResult(Long userId, Long departmentId, KpiGoals kpiGoals) {
        KpiResults r = KpiResults.builder()
            .achievedValue(null)
            .score(kpiScoreCalculator.computeScore(userId,departmentId,kpiGoals))
            .evaluatedAt(LocalDateTime.now())
            .kpiGoalId(kpiGoals.getKpiGoalId())
            .userId(userId)
            .departmentId(departmentId)
            .isDeleted(false)
            .build();

        kpiResultsRepository.save(r);
    }

    @Override
    @Transactional(readOnly = true)
    public KpiResultsResponse getResultById(Long kpiResultId) {
        KpiResults r = kpiResultsRepository.findByKpiResultIdAndIsDeletedFalse(kpiResultId)
            .orElseThrow(() -> new KpiResultsNotFoundException(kpiResultId));

        return KpiResultsResponse.from(r);
    }
}
