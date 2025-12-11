package com.j3s.yobuddy.domain.mentor.mentoring.repository;

import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringSession;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MentoringSessionQueryRepository {
    Page<MentoringSession> searchSessions(
        Long mentorId,
        Long menteeId,
        Long programId,
        MentoringStatus status,
        String query,
        Pageable pageable
    );
}
