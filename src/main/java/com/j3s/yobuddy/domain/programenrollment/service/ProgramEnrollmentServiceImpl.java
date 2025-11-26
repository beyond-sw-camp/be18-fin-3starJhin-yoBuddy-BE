package com.j3s.yobuddy.domain.programenrollment.service;

import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.programenrollment.dto.request.ProgramEnrollmentRequest;
import com.j3s.yobuddy.domain.programenrollment.dto.request.ProgramEnrollmentUpdateRequest;
import com.j3s.yobuddy.domain.programenrollment.dto.response.ProgramEnrollmentResponse;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.EnrollmentStatus;
import com.j3s.yobuddy.domain.programenrollment.exception.DuplicateEnrollmentException;
import com.j3s.yobuddy.domain.programenrollment.exception.EnrollmentNotFoundException;
import com.j3s.yobuddy.domain.programenrollment.repository.ProgramEnrollmentRepository;
import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import com.j3s.yobuddy.domain.task.repository.ProgramTaskRepository;
import com.j3s.yobuddy.domain.task.service.UserTaskAssignmentService;
import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import com.j3s.yobuddy.domain.training.repository.ProgramTrainingRepository;
import com.j3s.yobuddy.domain.training.service.UserTrainingAssignmentService;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import java.util.ArrayList;
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
    private final ProgramTrainingRepository programTrainingRepository;
    private final ProgramTaskRepository programTaskRepository;
    private final UserTrainingAssignmentService userTrainingAssignmentService;
    private final UserTaskAssignmentService userTaskAssignmentService;

    @Override
    @Transactional
    public List<ProgramEnrollmentResponse> enroll(Long programId, ProgramEnrollmentRequest request) {

        List<ProgramTraining> trainings =
            programTrainingRepository.findByProgram_ProgramId(programId);

        List<ProgramTask> tasks =
            programTaskRepository.findByOnboardingProgram_ProgramId(programId);

        List<ProgramEnrollmentResponse> result = new ArrayList<>();

        for (Long userId : request.getUserIds()) {

            if (enrollmentRepository.existsByUser_UserIdAndProgram_ProgramId(userId, programId)) {
                throw new DuplicateEnrollmentException(userId, programId);
            }

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new EnrollmentNotFoundException(userId));

            OnboardingProgram program = programRepository.findById(programId)
                .orElseThrow(() -> new EnrollmentNotFoundException(programId));

            ProgramEnrollment enrollment = ProgramEnrollment.builder()
                .user(user)
                .program(program)
                .status(EnrollmentStatus.ACTIVE)
                .build();

            enrollmentRepository.save(enrollment);

            userTrainingAssignmentService.assignForUser(user, trainings);

            userTaskAssignmentService.assignForUser(user, tasks);

            result.add(
                ProgramEnrollmentResponse.builder()
                    .enrollmentId(enrollment.getEnrollmentId())
                    .programId(programId)
                    .userId(user.getUserId())
                    .status(enrollment.getStatus())
                    .enrolledAt(enrollment.getEnrolledAt())
                    .build()
            );
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramEnrollmentResponse> getByProgram(Long programId) {
        return enrollmentRepository.findByProgram_ProgramId(programId)
            .stream()
            .map(e -> ProgramEnrollmentResponse.builder()
                .enrollmentId(e.getEnrollmentId())
                .programId(programId)
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
                .userId(userId)
                .status(e.getStatus())
                .enrolledAt(e.getEnrolledAt())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProgramEnrollmentResponse updateEnrollment(Long programId, Long enrollmentId,
        ProgramEnrollmentUpdateRequest request) {

        ProgramEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new EnrollmentNotFoundException(enrollmentId));

        enrollment.updateStatus(EnrollmentStatus.valueOf(request.getStatus()));

        return ProgramEnrollmentResponse.builder()
            .enrollmentId(enrollmentId)
            .programId(programId)
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
        enrollmentRepository.save(enrollment); }
}
