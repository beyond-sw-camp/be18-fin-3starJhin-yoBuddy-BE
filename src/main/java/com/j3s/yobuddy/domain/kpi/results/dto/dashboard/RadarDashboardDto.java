package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RadarDashboardDto {

    private String periodLabel; // "2025 상반기"
    private List<RadarDepartmentScoreDto> departments;
}
