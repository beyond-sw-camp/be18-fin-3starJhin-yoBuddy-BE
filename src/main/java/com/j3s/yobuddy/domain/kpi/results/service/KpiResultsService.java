package com.j3s.yobuddy.domain.kpi.results.service;

import java.util.List;

import com.j3s.yobuddy.domain.kpi.goals.entity.KpiGoals;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsListResponse;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsResponse;

public interface KpiResultsService {

    List<KpiResultsListResponse> getResults(Long kpiGoalId, Long userId, Long departmentId);

    void createResult(Long userId, Long departmentId,KpiGoals kpiGoals);

    KpiResultsResponse getResultById(Long kpiResultId);
    
    void  culculateKpiResults();
}