package com.j3s.yobuddy.domain.onboarding.dto.response;

import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram.ProgramStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OnboardingProgramResponse {

    private final Long programId;
    private final String name;
    private ProgramStatus status;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
