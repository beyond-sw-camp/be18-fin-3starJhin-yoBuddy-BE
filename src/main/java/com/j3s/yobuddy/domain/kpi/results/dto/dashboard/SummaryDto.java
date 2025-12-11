package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SummaryDto {
    private long pass;
    private long fail;
    private long totalUsers;
}