package com.j3s.yobuddy.domain.formresult.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class FormResultCreateRequest {

    private final String userName;
    private final String email;
    private final String onboardingName;
    private final String trainingName;
    private final BigDecimal score;
    private final BigDecimal maxScore;
    private final BigDecimal passingScore;
    private final LocalDateTime submittedAt;
}
