package com.j3s.yobuddy.domain.onboarding.dto.request;

import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram.ProgramStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OnboardingUpdateRequest {
    @NotBlank(message = "프로그램명은 필수입니다.")
    private final String name;

    private final String description;

    private final LocalDate startDate;

    private final LocalDate endDate;

    private final ProgramStatus status;
}
