package com.j3s.yobuddy.domain.mentoring.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MentoringSessionUpdateRequest {
    private final Boolean attended;
    private final String feedback;
    private final String scheduledAt;
}
