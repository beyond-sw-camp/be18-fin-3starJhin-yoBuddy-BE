package com.j3s.yobuddy.domain.mentor.mentoring.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MentoringSessionCreateRequest {
    private final Long mentorId;
    private final Long menteeId;
    private final String description;
    private final LocalDateTime scheduledAt;
}
