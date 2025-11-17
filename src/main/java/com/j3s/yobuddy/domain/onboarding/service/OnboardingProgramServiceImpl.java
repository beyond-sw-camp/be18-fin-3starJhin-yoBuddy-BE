package com.j3s.yobuddy.domain.onboarding.service;

import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.department.exception.DepartmentNotFoundException;
import com.j3s.yobuddy.domain.department.repository.DepartmentRepository;
import com.j3s.yobuddy.domain.onboarding.dto.request.OnboardingCreateRequest;
import com.j3s.yobuddy.domain.onboarding.dto.request.OnboardingUpdateRequest;
import com.j3s.yobuddy.domain.onboarding.dto.response.OnboardingProgramListResponse;
import com.j3s.yobuddy.domain.onboarding.dto.response.OnboardingProgramResponse;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.onboarding.exception.ProgramAlreadyDeletedException;
import com.j3s.yobuddy.domain.onboarding.exception.ProgramNotFoundException;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramQueryRepository;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OnboardingProgramServiceImpl implements OnboardingProgramService {
    private final OnboardingProgramRepository onboardingProgramRepository;
    private final OnboardingProgramQueryRepository onboardingProgramQueryRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public OnboardingProgram createOnboardingPrograms(OnboardingCreateRequest request) {

        Department department = departmentRepository.findByDepartmentIdAndIsDeletedFalse(
                                                         request.getDepartmentId())
                                                     .orElseThrow(
                                                         () -> new DepartmentNotFoundException(
                                                             request.getDepartmentId()));

        OnboardingProgram program = OnboardingProgram.builder()
                                                     .name(request.getName())
                                                     .description(request.getDescription())
                                                     .startDate(request.getStartDate())
                                                     .endDate(request.getEndDate())
                                                     .department(department)
                                                     .build();

        return onboardingProgramRepository.save(program);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OnboardingProgramListResponse> getAllPrograms() {
        return onboardingProgramQueryRepository.findProgramList();
    }

    @Override
    @Transactional(readOnly = true)
    public OnboardingProgramResponse getProgramById(Long programId) {

        OnboardingProgram program = onboardingProgramRepository.findByProgramIdAndDeletedFalse(
                                                                   programId)
                                                               .orElseThrow(
                                                                   () -> new ProgramNotFoundException(
                                                                       programId));

        return OnboardingProgramResponse.builder()
                                        .programId(program.getProgramId())
                                        .name(program.getName())
                                        .description(program.getDescription())
                                        .startDate(program.getStartDate())
                                        .endDate(program.getEndDate())
                                        .createdAt(program.getCreatedAt())
                                        .updatedAt(program.getUpdatedAt())
                                        .build();
    }

    @Override
    @Transactional
    public OnboardingProgramResponse updateProgram(Long programId,
        OnboardingUpdateRequest request) {

        OnboardingProgram program = onboardingProgramRepository.findByProgramIdAndDeletedFalse(
                                                                   programId)
                                                               .orElseThrow(
                                                                   () -> new ProgramNotFoundException(
                                                                       programId));

        program.update(request.getName(), request.getDescription(), request.getStartDate(),
            request.getEndDate());

        onboardingProgramRepository.save(program);

        return OnboardingProgramResponse.builder()
                                        .programId(program.getProgramId())
                                        .name(program.getName())
                                        .description(program.getDescription())
                                        .startDate(program.getStartDate())
                                        .endDate(program.getEndDate())
                                        .createdAt(program.getCreatedAt())
                                        .updatedAt(program.getUpdatedAt())
                                        .build();
    }

    @Override
    @Transactional
    public void softDeleteProgram(Long programId) {

        OnboardingProgram program = onboardingProgramRepository.findByProgramIdAndDeletedFalse(
                                                                   programId)
                                                               .orElseThrow(
                                                                   () -> new ProgramNotFoundException(
                                                                       programId));
        if (program.isDeleted()) {
            throw new ProgramAlreadyDeletedException(programId);
        }
        program.softDelete();
        onboardingProgramRepository.save(program);
    }
}
