package com.j3s.yobuddy.domain.programenrollment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment;

@Repository
public interface ProgramEnrollmentRepository extends JpaRepository<ProgramEnrollment, Long> {
    List<ProgramEnrollment> findByProgram_ProgramId(Long programId);
    List<ProgramEnrollment> findByUser_UserId(Long userId);
    boolean existsByUser_UserIdAndProgram_ProgramId(Long userId, Long programId);
    Optional<ProgramEnrollment> findByUser_UserIdAndProgram_ProgramId(Long userId, Long programId);
    Optional<ProgramEnrollment> findByUser_UserIdAndStatus(Long userId, ProgramEnrollment.EnrollmentStatus status);
}
