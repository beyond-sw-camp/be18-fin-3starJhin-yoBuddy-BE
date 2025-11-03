package com.j3s.yobuddy.domain.programenrollment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProgramEnrollmentRequest {

    @NotNull(message = "프로그램 ID는 필수입니다.")
    private Long programId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
}