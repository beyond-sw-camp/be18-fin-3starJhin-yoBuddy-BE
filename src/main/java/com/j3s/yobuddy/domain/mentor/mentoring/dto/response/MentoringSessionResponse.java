package com.j3s.yobuddy.domain.mentor.mentoring.dto.response;

import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;
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

    private final String mentorName;
    private final String menteeName;
    private final String menteeEmail;
    private final String menteePhoneNumber;

    private final String menteeProfileImageUrl;

    private final String description;
    private final LocalDateTime scheduledAt;
    private final MentoringStatus status;
    private final String feedback;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
