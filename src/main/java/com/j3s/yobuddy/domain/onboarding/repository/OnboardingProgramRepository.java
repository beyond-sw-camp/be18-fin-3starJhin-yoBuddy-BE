package com.j3s.yobuddy.domain.onboarding.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;

@Repository
public interface OnboardingProgramRepository extends JpaRepository<OnboardingProgram, Long> {
    Optional<OnboardingProgram> findByProgramIdAndDeletedFalse(Long programId);
}
