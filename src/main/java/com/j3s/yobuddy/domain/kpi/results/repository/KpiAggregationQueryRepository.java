package com.j3s.yobuddy.domain.kpi.results.repository;

import com.querydsl.core.Tuple;
import java.time.LocalDateTime;
import java.util.List;

public interface KpiAggregationQueryRepository {

    List<Tuple> aggregateTrainingCompletion(Long programId);

    List<Tuple> aggregateTaskSubmitRate(Long programId);

    List<Tuple> aggregateAvgTaskScore(Long programId);

    List<Tuple> aggregateOfflineAttendance(Long programId);

    List<Tuple> aggregateWeeklyReportByPeriod(
        Long programId,
        LocalDateTime startDt,
        LocalDateTime endDt
    );
}
