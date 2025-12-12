package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentDashboardDto {

    private Long departmentId;
    private String departmentName;

    private BigDecimal currentAvgKpi;
    private BigDecimal targetAvgKpi;

    private long mentoringCount;

    private TaskStatusDto taskStatus;
    private EducationStatusDto educationStatus;
}
