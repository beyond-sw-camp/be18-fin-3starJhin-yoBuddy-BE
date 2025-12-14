package com.j3s.yobuddy.domain.kpi.results.service;

import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsListResponse;
import com.j3s.yobuddy.domain.kpi.results.dto.response.KpiResultsResponse;
import java.util.List;

public interface KpiResultsService {

    List<KpiResultsListResponse> getResults(Long kpiGoalId, Long userId, Long departmentId);

    KpiResultsResponse getResultById(Long kpiResultId);

    void culculateKpiResults();

    void calculateKpiResults(boolean includePastPrograms, boolean forceRecalculate);
}
