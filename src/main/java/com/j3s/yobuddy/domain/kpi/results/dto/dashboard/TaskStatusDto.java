package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskStatusDto {
    // 과제 제출 기한 준수 현황
    private int onTimePercent;    // 정상 제출 %
    private int latePercent;      // 지연 제출 %
}
