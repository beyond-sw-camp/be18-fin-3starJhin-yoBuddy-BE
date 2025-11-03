package com.j3s.yobuddy.domain.onboarding.service;

import com.j3s.yobuddy.domain.onboarding.dto.request.OnboardingCreateRequest;
import com.j3s.yobuddy.domain.onboarding.dto.request.OnboardingUpdateRequest;
import com.j3s.yobuddy.domain.onboarding.dto.response.OnboardingProgramListResponse;
import com.j3s.yobuddy.domain.onboarding.dto.response.OnboardingProgramResponse;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingPrograms;
import com.j3s.yobuddy.domain.onboarding.exception.ProgramAlreadyDeletedException;
import com.j3s.yobuddy.domain.onboarding.exception.ProgramNotFoundException;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OnboardingProgramServiceImpl implements OnboardingProgramService {
    private final OnboardingProgramRepository onboardingProgramRepository;

    @Override
    public OnboardingPrograms createOnboardingPrograms(OnboardingCreateRequest request) {

        OnboardingPrograms program = OnboardingPrograms.builder()
                                                       .name(request.getName())
                                                       .description(request.getDescription())
                                                       .startDate(request.getStartDate())
                                                       .endDate(request.getEndDate())
                                                       .build();

        return onboardingProgramRepository.save(program);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OnboardingProgramListResponse> getAllPrograms() {

        return onboardingProgramRepository.findAllByDeletedFalse()
                                          .stream()
                                          .map(program -> OnboardingProgramListResponse.builder()
                                                                                       .programId(program.getProgramId())
                                                                                       .name(program.getName())
                                                                                       .startDate(program.getStartDate())
                                                                                       .endDate(program.getEndDate())
                                                                                       .build()
                                          )
                                          .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OnboardingProgramResponse getProgramById(Long programId) {
        OnboardingPrograms program = onboardingProgramRepository.findByProgramIdAndDeletedFalse(programId)
                                                                .orElseThrow(() -> new ProgramNotFoundException(programId));

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
    public OnboardingProgramResponse updateProgram(Long programId, OnboardingUpdateRequest request) {
        OnboardingPrograms program = onboardingProgramRepository.findByProgramIdAndDeletedFalse(programId)
                                                                .orElseThrow(() -> new ProgramNotFoundException(programId));

        program.update(request.getName(), request.getDescription(), request.getStartDate(), request.getEndDate());

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
    public void softDeleteProgram(Long programId) {
        OnboardingPrograms program = onboardingProgramRepository.findByProgramIdAndDeletedFalse(programId)
                                                                .orElseThrow(() -> new ProgramNotFoundException(programId));
        if (program.isDeleted()) {
            throw new ProgramAlreadyDeletedException(programId);
        }
        program.softDelete();
        onboardingProgramRepository.save(program);
    }
}
