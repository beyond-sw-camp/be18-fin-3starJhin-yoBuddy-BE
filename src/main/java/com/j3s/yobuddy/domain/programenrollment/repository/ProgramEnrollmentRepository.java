package com.j3s.yobuddy.domain.programenrollment.repository;

import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.EnrollmentStatus;
import com.j3s.yobuddy.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramEnrollmentRepository extends JpaRepository<ProgramEnrollment, Long> {
    List<ProgramEnrollment> findByProgram_ProgramId(Long programId);

    List<ProgramEnrollment> findByUser_UserId(Long userId);

    boolean existsByUser_UserIdAndProgram_ProgramId(Long userId, Long programId);

    Optional<ProgramEnrollment> findByUser_UserIdAndProgram_ProgramId(Long userId, Long programId);

    Optional<ProgramEnrollment> findByUser_UserIdAndStatus(Long userId,
        ProgramEnrollment.EnrollmentStatus status);

    List<ProgramEnrollment> findByProgram_ProgramIdAndStatus(Long programId,
        EnrollmentStatus status);

    List<ProgramEnrollment> findByStatus(ProgramEnrollment.EnrollmentStatus status);

    List<ProgramEnrollment> findByProgram_ProgramIdAndStatusIn(
        Long programId,
        List<ProgramEnrollment.EnrollmentStatus> statuses
    );
}
