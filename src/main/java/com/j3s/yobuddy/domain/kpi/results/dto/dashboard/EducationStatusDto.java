package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EducationStatusDto {
    // 교육 이수 현황
    private int completedPercent;    // 정상 이수 %
    private int notCompletedPercent; // 미이수 %
}