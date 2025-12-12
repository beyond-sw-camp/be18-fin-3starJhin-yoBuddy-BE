package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardOverviewResponse {

    private TopSummaryDto summary;                        // 상단 카운터 박스
    private List<DepartmentDashboardDto> departments;     // 부서별 바/파이/멘토링
    private RadarDashboardDto radar;                      // 부서별 레이더
}
