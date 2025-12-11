package com.j3s.yobuddy.domain.kpi.results.dto.dashboard;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentoringSummaryDto {

    private final long total;
    private final long completed;
    private final long scheduled;
    private final long cancelled;
    private final long noShow;

    public static MentoringSummaryDto empty() {
        return MentoringSummaryDto.builder()
            .total(0)
            .completed(0)
            .scheduled(0)
            .cancelled(0)
            .noShow(0)
            .build();
    }
}