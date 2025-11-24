package com.j3s.yobuddy.domain.onboarding.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
public class OnboardingCreateRequest {

    @NotBlank(message = "프로그램명은 필수입니다.")
    private final String name;

    private final String description;

    private Long departmentId;

    private final LocalDate startDate;

    private final LocalDate endDate;
}
