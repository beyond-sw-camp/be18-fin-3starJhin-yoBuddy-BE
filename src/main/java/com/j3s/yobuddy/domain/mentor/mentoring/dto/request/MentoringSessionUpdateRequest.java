package com.j3s.yobuddy.domain.mentor.mentoring.dto.request;

import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MentoringSessionUpdateRequest {
    private final MentoringStatus status;
    private final String description;
    private final String feedback;
    private final String scheduledAt;
}
