package com.j3s.yobuddy.domain.onboarding.service;

import com.j3s.yobuddy.domain.onboarding.dto.request.OnboardingCreateRequest;
import com.j3s.yobuddy.domain.onboarding.dto.request.OnboardingUpdateRequest;
import com.j3s.yobuddy.domain.onboarding.dto.response.OnboardingProgramListResponse;
import com.j3s.yobuddy.domain.onboarding.dto.response.OnboardingProgramResponse;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingPrograms;
import java.util.List;

public interface OnboardingProgramService {
    OnboardingPrograms createOnboardingPrograms(OnboardingCreateRequest request);
    List<OnboardingProgramListResponse> getAllPrograms();
    OnboardingProgramResponse getProgramById(Long programId);
    OnboardingProgramResponse updateProgram(Long programId, OnboardingUpdateRequest request);
    void softDeleteProgram(Long programId);
}
