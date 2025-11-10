package com.j3s.yobuddy.domain.programenrollment.service;

import com.j3s.yobuddy.domain.onboarding.entity.OnboardingPrograms;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.programenrollment.dto.request.ProgramEnrollmentRequest;
import com.j3s.yobuddy.domain.programenrollment.dto.request.ProgramEnrollmentUpdateRequest;
import com.j3s.yobuddy.domain.programenrollment.dto.response.ProgramEnrollmentResponse;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.EnrollmentStatus;
import com.j3s.yobuddy.domain.programenrollment.exception.DuplicateEnrollmentException;
import com.j3s.yobuddy.domain.programenrollment.exception.EnrollmentNotFoundException;
import com.j3s.yobuddy.domain.programenrollment.repository.ProgramEnrollmentRepository;
import com.j3s.yobuddy.domain.user.entity.Users;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgramEnrollmentServiceImpl implements ProgramEnrollmentService {

    private final ProgramEnrollmentRepository enrollmentRepository;
    private final OnboardingProgramRepository programRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProgramEnrollmentResponse enroll(ProgramEnrollmentRequest request) {
        if (enrollmentRepository.existsByUser_UserIdAndProgram_ProgramId(request.getUserId(), request.getProgramId())) {
            throw new DuplicateEnrollmentException(request.getUserId(), request.getProgramId());
        }

        Users user = userRepository.findById(request.getUserId())
                                  .orElseThrow(() -> new EnrollmentNotFoundException(request.getUserId()));
        OnboardingPrograms program = programRepository.findById(request.getProgramId())
                                                      .orElseThrow(() -> new EnrollmentNotFoundException(request.getProgramId()));

        ProgramEnrollment enrollment = ProgramEnrollment.builder()
                                                        .user(user)
                                                        .program(program)
                                                        .status(EnrollmentStatus.ACTIVE)
                                                        .build();

        ProgramEnrollment saved = enrollmentRepository.save(enrollment);

        return ProgramEnrollmentResponse.builder()
                                        .enrollmentId(saved.getEnrollmentId())
                                        .programId(program.getProgramId())
                                        .userId(user.getUserId())
                                        .status(saved.getStatus())
                                        .enrolledAt(saved.getEnrolledAt())
                                        .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramEnrollmentResponse> getByProgram(Long programId) {
        return enrollmentRepository.findByProgram_ProgramId(programId)
                                   .stream()
                                   .map(e -> ProgramEnrollmentResponse.builder()
                                                                      .enrollmentId(e.getEnrollmentId())
                                                                      .programId(e.getProgram().getProgramId())
                                                                      .userId(e.getUser().getUserId())
                                                                      .status(e.getStatus())
                                                                      .enrolledAt(e.getEnrolledAt())
                                                                      .build())
                                   .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramEnrollmentResponse> getByUser(Long userId) {
        return enrollmentRepository.findByUser_UserId(userId)
                                   .stream()
                                   .map(e -> ProgramEnrollmentResponse.builder()
                                                                      .enrollmentId(e.getEnrollmentId())
                                                                      .programId(e.getProgram().getProgramId())
                                                                      .userId(e.getUser().getUserId())
                                                                      .status(e.getStatus())
                                                                      .enrolledAt(e.getEnrolledAt())
                                                                      .build())
                                   .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProgramEnrollmentResponse updateEnrollment(Long id, ProgramEnrollmentUpdateRequest request) {
        ProgramEnrollment enrollment = enrollmentRepository.findById(id)
                                                           .orElseThrow(() -> new EnrollmentNotFoundException(id));

        enrollment.updateStatus(EnrollmentStatus.valueOf(request.getStatus()));
        enrollmentRepository.save(enrollment);

        return ProgramEnrollmentResponse.builder()
                                        .enrollmentId(enrollment.getEnrollmentId())
                                        .programId(enrollment.getProgram().getProgramId())
                                        .userId(enrollment.getUser().getUserId())
                                        .status(enrollment.getStatus())
                                        .enrolledAt(enrollment.getEnrolledAt())
                                        .build();
    }

    @Override
    @Transactional
    public void withdraw(Long id) {
        ProgramEnrollment enrollment = enrollmentRepository.findById(id)
                                                           .orElseThrow(() -> new EnrollmentNotFoundException(id));

        enrollment.updateStatus(EnrollmentStatus.WITHDRAWN);
        enrollmentRepository.save(enrollment);
    }
}
