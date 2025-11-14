package com.j3s.yobuddy.domain.onboarding.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OnboardingProgramListResponse {
    private Long programId;
    private String name;
    private String department;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer participantCount;
    private Double completionRate;
    private LocalDateTime createdAt;
}
