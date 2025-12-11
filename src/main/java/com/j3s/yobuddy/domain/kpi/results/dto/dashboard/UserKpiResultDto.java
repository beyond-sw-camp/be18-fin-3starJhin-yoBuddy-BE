package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import com.j3s.yobuddy.domain.kpi.results.entity.KpiResults;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserKpiResultDto {

    private Long kpiResultId;
    private Long kpiGoalId;
    private BigDecimal score;
    private LocalDateTime evaluatedAt;
    private Integer year;

    public static UserKpiResultDto from(KpiResults r) {
        Integer year = (r.getEvaluatedAt() != null) ? r.getEvaluatedAt().getYear() : null;
        return UserKpiResultDto.builder()
            .kpiResultId(r.getKpiResultId())
            .kpiGoalId(r.getKpiGoalId())
            .score(r.getScore())
            .evaluatedAt(r.getEvaluatedAt())
            .year(year)
            .build();
    }
}
