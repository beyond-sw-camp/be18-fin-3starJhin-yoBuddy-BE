package com.j3s.yobuddy.domain.onboarding.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OnboardingProgramListResponse {
    private final Long programId;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
}
