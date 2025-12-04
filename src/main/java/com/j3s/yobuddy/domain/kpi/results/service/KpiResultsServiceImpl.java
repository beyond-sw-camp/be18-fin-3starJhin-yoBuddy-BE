package com.j3s.yobuddy.domain.kpi.results.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.kpi.results.dto.request.KpiResultsRequest;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsListResponse;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsResponse;
import com.j3s.yobuddy.domain.kpi.results.entity.KpiResults;
import com.j3s.yobuddy.domain.kpi.results.exception.KpiResultsAlreadyDeletedException;
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
    public void createResult(KpiResultsRequest request) {
        KpiResults r = KpiResults.builder()
            .achievedValue(request.getAchievedValue())
            .score(kpiScoreCalculator.computeScore(request))
            .evaluatedAt(request.getEvaluatedAt())
            .kpiGoalId(request.getKpiGoalId())
            .userId(request.getUserId())
            .departmentId(request.getDepartmentId())
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

    @Override
    @Transactional
    public KpiResultsListResponse updateResult(Long kpiResultId, KpiResultsRequest request) {
        KpiResults r = kpiResultsRepository.findByKpiResultIdAndIsDeletedFalse(kpiResultId)
            .orElseThrow(() -> new KpiResultsNotFoundException(kpiResultId));

        if (r.getIsDeleted()) {
            throw new KpiResultsAlreadyDeletedException(kpiResultId);
        }

        // update 시에도 계산기를 통해 score를 재계산
        r.update(request.getAchievedValue(), kpiScoreCalculator.computeScore(request), request.getEvaluatedAt(),
            request.getKpiGoalId(), request.getUserId(), request.getDepartmentId());

        kpiResultsRepository.save(r);

        return KpiResultsListResponse.builder()
            .kpiResultId(r.getKpiResultId())
            .achievedValue(r.getAchievedValue())
            .score(r.getScore())
            .evaluatedAt(r.getEvaluatedAt())
            .kpiGoalId(r.getKpiGoalId())
            .userId(r.getUserId())
            .departmentId(r.getDepartmentId())
            .createdAt(r.getCreatedAt())
            .updatedAt(r.getUpdatedAt())
            .build();
    }
}
