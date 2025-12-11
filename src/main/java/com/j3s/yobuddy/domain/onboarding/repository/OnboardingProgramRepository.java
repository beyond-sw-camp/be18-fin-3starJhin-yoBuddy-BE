package com.j3s.yobuddy.domain.onboarding.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;

@Repository
public interface OnboardingProgramRepository extends JpaRepository<OnboardingProgram, Long> {

    Optional<OnboardingProgram> findByProgramIdAndDeletedFalse(Long programId);

    Optional<OnboardingProgram> findByName(String onboardingName);

    List<OnboardingProgram> findByDeletedFalseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        LocalDate start, LocalDate end
    );

    // find programs by department id
    List<OnboardingProgram> findByDepartment_DepartmentIdAndDeletedFalse(Long departmentId);
}
