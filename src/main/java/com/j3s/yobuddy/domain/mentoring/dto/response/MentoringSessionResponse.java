package com.j3s.yobuddy.domain.mentoring.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MentoringSessionResponse {

    private final Long id;
    private final Long mentorId;
    private final Long menteeId;
    private final Long programId;

    private final String menteeName;
    private final String menteeEmail;
    private final String menteePhoneNumber;

    private final LocalDateTime scheduledAt;
    private final Boolean attended;
    private final String feedback;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
