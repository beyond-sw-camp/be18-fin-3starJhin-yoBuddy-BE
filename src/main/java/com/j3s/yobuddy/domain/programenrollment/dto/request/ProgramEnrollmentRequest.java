package com.j3s.yobuddy.domain.programenrollment.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProgramEnrollmentRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private final List<Long> userIds;
}