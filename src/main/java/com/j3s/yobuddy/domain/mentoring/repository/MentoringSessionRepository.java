package com.j3s.yobuddy.domain.mentoring.repository;

import com.j3s.yobuddy.domain.mentoring.entity.MentoringSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long> {

    List<MentoringSession> findByMentor_UserIdAndDeletedFalse(Long mentorId);

    List<MentoringSession> findByMentee_UserIdAndDeletedFalse(Long menteeId);

    List<MentoringSession> findByProgram_ProgramIdAndDeletedFalse(Long programId);

    Optional<MentoringSession> findByIdAndDeletedFalse(Long sessionId);

    List<MentoringSession> findAllByDeletedFalse();
}