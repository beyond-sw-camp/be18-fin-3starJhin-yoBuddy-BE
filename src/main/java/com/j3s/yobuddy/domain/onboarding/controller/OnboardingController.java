package com.j3s.yobuddy.domain.onboarding.controller;

import com.j3s.yobuddy.domain.onboarding.dto.request.OnboardingCreateRequest;
import com.j3s.yobuddy.domain.onboarding.dto.request.OnboardingUpdateRequest;
import com.j3s.yobuddy.domain.onboarding.dto.response.OnboardingProgramListResponse;
import com.j3s.yobuddy.domain.onboarding.dto.response.OnboardingProgramResponse;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingPrograms;
import com.j3s.yobuddy.domain.onboarding.service.OnboardingProgramService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/onboarding")
public class OnboardingController {
    private final OnboardingProgramService onboardingProgramService;

    @PostMapping
    public ResponseEntity<Void> createProgram(@Valid @RequestBody OnboardingCreateRequest request) {
        OnboardingPrograms created = onboardingProgramService.createOnboardingPrograms(request);
        return ResponseEntity
            .created(URI.create("/api/v1/onboarding/" + created.getProgramId()))
            .build();
    }

    @GetMapping
    public ResponseEntity<List<OnboardingProgramListResponse>> getAllPrograms() {
        List<OnboardingProgramListResponse> programs = onboardingProgramService.getAllPrograms();
        return ResponseEntity.ok(programs);
    }

    @GetMapping("/{programId}")
    public ResponseEntity<OnboardingProgramResponse> getProgramById(@PathVariable Long programId) {
        OnboardingProgramResponse program = onboardingProgramService.getProgramById(programId);
        return ResponseEntity.ok(program);
    }

    @PatchMapping("/{programId}")
    public ResponseEntity<OnboardingProgramResponse> updateProgram(
        @PathVariable Long programId,
        @Valid @RequestBody OnboardingUpdateRequest request
    ) {
        OnboardingProgramResponse updated = onboardingProgramService.updateProgram(programId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{programId}")
    public ResponseEntity<Void> softDeleteProgram(@PathVariable Long programId) {
        onboardingProgramService.softDeleteProgram(programId);
        return ResponseEntity.noContent().build();
    }
}
