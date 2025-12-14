package com.j3s.yobuddy.domain.kpi.results.service;

import com.j3s.yobuddy.domain.kpi.results.repository.KpiAggregationQueryRepository;
import com.querydsl.core.Tuple;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KpiAggregationService {

    private final KpiAggregationQueryRepository repo;

    public KpiAggregatedResult aggregate(
        Long programId,
        LocalDateTime startDt,
        LocalDateTime endDt
    ) {
        KpiAggregatedResult result = new KpiAggregatedResult();

        /* 1️⃣ 교육 이수율 (category = 1) */
        for (Tuple t : repo.aggregateTrainingCompletion(programId)) {
            Long userId = t.get(0, Long.class);
            Long total = t.get(1, Long.class);
            Long completed = t.get(2, Long.class);

            BigDecimal rate = percent(completed, total);
            result.put(userId, 1L, rate);
        }

        /* 2️⃣ 과제 제출률 (category = 2) */
        for (Tuple t : repo.aggregateTaskSubmitRate(programId)) {
            Long userId = t.get(0, Long.class);
            Long total = t.get(1, Long.class);
            Long submitted = t.get(2, Long.class);

            BigDecimal rate = percent(submitted, total);
            result.put(userId, 2L, rate);
        }

        /* 3️⃣ 과제 평균 점수 (category = 3) */
        for (Tuple t : repo.aggregateAvgTaskScore(programId)) {
            Long userId = t.get(0, Long.class);
            Double avgDouble = t.get(1, Double.class);

            BigDecimal avg = avgDouble == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(avgDouble);

            result.put(userId, 3L, avg);
        }

        /* 4️⃣ 오프라인 참여율 (category = 5) */
        for (Tuple t : repo.aggregateOfflineAttendance(programId)) {
            Long userId = t.get(0, Long.class);
            Long total = t.get(1, Long.class);
            Long completed = t.get(2, Long.class);

            BigDecimal rate = percent(completed, total);
            result.put(userId, 5L, rate);
        }

        /* 5️⃣ 주간보고 제출률 (category = 4) */
        for (Tuple t : repo.aggregateWeeklyReportByPeriod(programId, startDt, endDt)) {
            Long userId = t.get(0, Long.class);
            Number submittedWeeksNum = t.get(1, Number.class);
            Number maxWeekNum = t.get(2, Number.class);

            long submittedWeeks = submittedWeeksNum == null ? 0L : submittedWeeksNum.longValue();
            long maxWeek = maxWeekNum == null ? 0L : maxWeekNum.longValue();

            BigDecimal rate = percent(submittedWeeks, maxWeek);
            result.put(userId, 4L, rate);
        }

        return result;
    }

    private BigDecimal percent(Long num, Long den) {
        if (den == null || den == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(num == null ? 0 : num)
            .divide(BigDecimal.valueOf(den), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
