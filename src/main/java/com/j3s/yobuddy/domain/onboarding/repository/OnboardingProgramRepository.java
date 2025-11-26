package com.j3s.yobuddy.domain.onboarding.repository;

import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnboardingProgramRepository extends JpaRepository<OnboardingProgram, Long> {

    Optional<OnboardingProgram> findByProgramIdAndDeletedFalse(Long programId);

    Optional<OnboardingProgram> findByName(String onboardingName);
}
