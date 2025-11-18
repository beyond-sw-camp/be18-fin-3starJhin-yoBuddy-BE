package com.j3s.yobuddy.domain.kpi.goals.service;

import java.util.List;

import com.j3s.yobuddy.domain.kpi.goals.dto.request.KpiGoalsRequest;
import com.j3s.yobuddy.domain.kpi.goals.dto.response.KpiGoalsListResponse;
import com.j3s.yobuddy.domain.kpi.goals.dto.response.KpiGoalsResponse;

public interface KpiGoalsService {

    List<KpiGoalsListResponse> getGoals(String description);

    void createGoal(KpiGoalsRequest request);

    KpiGoalsListResponse updateGoal(Long kpiGoalId, KpiGoalsRequest request);

    void deleteGoal(Long kpiGoalId);

    KpiGoalsResponse getGoalById(Long kpiGoalId);
}
