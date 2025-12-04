package com.j3s.yobuddy.domain.kpi.results.service;

import java.util.List;

import com.j3s.yobuddy.domain.kpi.results.dto.request.KpiResultsRequest;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsListResponse;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsResponse;

public interface KpiResultsService {

    List<KpiResultsListResponse> getResults(Long kpiGoalId, Long userId, Long departmentId);

    void createResult(KpiResultsRequest request);

    KpiResultsResponse getResultById(Long kpiResultId);

    KpiResultsListResponse updateResult(Long kpiResultId, KpiResultsRequest request);
}
