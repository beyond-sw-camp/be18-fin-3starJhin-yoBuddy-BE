package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KpiDashboardResponse {

    private Long departmentId;
    private String departmentName;

    private List<KpiGoalDto> goals;
    private List<UserDashboardDto> users;

    private SummaryDto summary;
    private MentoringSummaryDto mentoring;
    private DashboardChartDto chart;
}
