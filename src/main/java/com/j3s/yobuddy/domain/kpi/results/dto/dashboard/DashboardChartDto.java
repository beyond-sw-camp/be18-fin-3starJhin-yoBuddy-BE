package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardChartDto {

    private List<Integer> years;
    private List<BigDecimal> achievementPerYear;
    private List<RadarPointDto> radar;
}
