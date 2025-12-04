package com.j3s.yobuddy.domain.mentor.mentoring.repository;

import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringSession;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long>, MentoringSessionQueryRepository {

    Page<MentoringSession> findByMentor_UserIdAndDeletedFalse(Long mentorId, Pageable pageable);

    Page<MentoringSession> findByMentee_UserIdAndDeletedFalse(Long menteeId, Pageable pageable);

    Page<MentoringSession> findByProgram_ProgramIdAndDeletedFalse(Long programId, Pageable pageable);

    Page<MentoringSession> findAllByDeletedFalse(Pageable pageable);

    Optional<MentoringSession> findByIdAndDeletedFalse(Long sessionId);

    List<MentoringSession> findByScheduledAtBetweenAndDeletedFalseAndStatus(
        LocalDateTime start,
        LocalDateTime end,
        MentoringStatus status
    );
}
