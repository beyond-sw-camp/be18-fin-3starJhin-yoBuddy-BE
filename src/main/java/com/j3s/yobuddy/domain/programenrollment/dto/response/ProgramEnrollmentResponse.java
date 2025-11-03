package com.j3s.yobuddy.domain.programenrollment.dto.response;

import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.EnrollmentStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProgramEnrollmentResponse {
    private Long enrollmentId;
    private Long programId;
    private Long userId;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;
}