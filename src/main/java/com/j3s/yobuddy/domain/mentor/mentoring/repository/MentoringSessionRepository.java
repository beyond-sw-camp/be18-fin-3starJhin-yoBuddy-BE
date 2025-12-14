package com.j3s.yobuddy.domain.mentor.mentoring.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringSession;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;

public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long>, MentoringSessionQueryRepository {

    Page<MentoringSession> findByMentor_UserIdAndDeletedFalse(Long mentorId, Pageable pageable);

    Page<MentoringSession> findByMentee_UserIdAndDeletedFalse(Long menteeId, Pageable pageable);

    Page<MentoringSession> findByProgram_ProgramIdAndDeletedFalse(Long programId, Pageable pageable);

    // find sessions by nested program.department.departmentId
    List<MentoringSession> findByProgram_Department_DepartmentIdAndDeletedFalse(Long departmentId);

    List<MentoringSession> findByProgram_ProgramIdInAndDeletedFalse(List<Long> programIds);

    Page<MentoringSession> findAllByDeletedFalse(Pageable pageable);

    Optional<MentoringSession> findByIdAndDeletedFalse(Long sessionId);

    List<MentoringSession> findByScheduledAtBetweenAndDeletedFalseAndStatus(
        LocalDateTime start,
        LocalDateTime end,
        MentoringStatus status
    );

    long countByMentee_UserIdAndProgram_ProgramIdAndDeletedFalse(
        Long menteeId,
        Long programId
    );

    /**
     * 결석 멘토링 세션 수
     */
    long countByMentee_UserIdAndProgram_ProgramIdAndStatusInAndDeletedFalse(
        Long menteeId,
        Long programId,
        List<MentoringStatus> statuses
    );
}
