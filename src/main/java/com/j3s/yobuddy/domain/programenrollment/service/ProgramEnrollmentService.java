package com.j3s.yobuddy.domain.programenrollment.service;

import java.util.List;

import com.j3s.yobuddy.domain.programenrollment.dto.request.ProgramEnrollmentRequest;
import com.j3s.yobuddy.domain.programenrollment.dto.request.ProgramEnrollmentUpdateRequest;
import com.j3s.yobuddy.domain.programenrollment.dto.response.ProgramEnrollmentResponse;

public interface ProgramEnrollmentService {

    List<ProgramEnrollmentResponse> enroll(Long programId, ProgramEnrollmentRequest request);

    List<ProgramEnrollmentResponse> getByProgram(Long programId);

    List<ProgramEnrollmentResponse> getByUser(Long userId);

    ProgramEnrollmentResponse updateEnrollment(Long programId, Long enrollmentId, ProgramEnrollmentUpdateRequest request);

    void withdraw(Long programId, Long enrollmentId);
}
