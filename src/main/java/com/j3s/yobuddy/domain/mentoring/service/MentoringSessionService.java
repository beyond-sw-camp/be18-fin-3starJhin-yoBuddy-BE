package com.j3s.yobuddy.domain.mentoring.service;

import com.j3s.yobuddy.domain.mentoring.dto.request.MentoringSessionCreateRequest;
import com.j3s.yobuddy.domain.mentoring.dto.request.MentoringSessionUpdateRequest;
import com.j3s.yobuddy.domain.mentoring.dto.response.MentoringSessionResponse;
import com.j3s.yobuddy.domain.mentoring.entity.MentoringStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MentoringSessionService {
    MentoringSessionResponse create(MentoringSessionCreateRequest request);

    MentoringSessionResponse get(Long sessionId);

    Page<MentoringSessionResponse> getByMentor(Long mentorId, Pageable pageable);

    Page<MentoringSessionResponse> getByMentee(Long menteeId, Pageable pageable);

    Page<MentoringSessionResponse> getByProgram(Long programId, Pageable pageable);

    Page<MentoringSessionResponse> getAll(Pageable pageable);

    MentoringSessionResponse update(Long sessionId, MentoringSessionUpdateRequest request);

    void delete(Long sessionId);

    Page<MentoringSessionResponse> searchSessions(
        Long mentorId,
        Long menteeId,
        Long programId,
        MentoringStatus status,
        String query,
        Pageable pageable
    );
}
