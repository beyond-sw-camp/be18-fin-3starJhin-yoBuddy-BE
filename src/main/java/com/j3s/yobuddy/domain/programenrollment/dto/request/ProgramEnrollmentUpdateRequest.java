package com.j3s.yobuddy.domain.programenrollment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramEnrollmentUpdateRequest {

    @NotBlank(message = "상태 값은 필수입니다.")
    private String status;
}