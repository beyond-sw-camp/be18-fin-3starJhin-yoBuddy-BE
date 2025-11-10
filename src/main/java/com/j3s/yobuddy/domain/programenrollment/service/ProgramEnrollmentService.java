package com.j3s.yobuddy.domain.programenrollment.service;

import com.j3s.yobuddy.domain.programenrollment.dto.request.ProgramEnrollmentRequest;
import com.j3s.yobuddy.domain.programenrollment.dto.request.ProgramEnrollmentUpdateRequest;
import com.j3s.yobuddy.domain.programenrollment.dto.response.ProgramEnrollmentResponse;
import java.util.List;

public interface ProgramEnrollmentService {
    ProgramEnrollmentResponse enroll(ProgramEnrollmentRequest request);
    List<ProgramEnrollmentResponse> getByProgram(Long programId);
    List<ProgramEnrollmentResponse> getByUser(Long userId);
    ProgramEnrollmentResponse updateEnrollment(Long id, ProgramEnrollmentUpdateRequest request);
    void withdraw(Long id);
}
