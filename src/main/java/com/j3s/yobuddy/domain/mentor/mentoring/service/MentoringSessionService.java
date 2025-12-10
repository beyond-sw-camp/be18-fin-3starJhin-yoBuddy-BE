package com.j3s.yobuddy.domain.mentor.mentoring.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.j3s.yobuddy.domain.mentor.mentoring.dto.request.MentoringSessionCreateRequest;
import com.j3s.yobuddy.domain.mentor.mentoring.dto.request.MentoringSessionUpdateRequest;
import com.j3s.yobuddy.domain.mentor.mentoring.dto.response.MentoringSessionResponse;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;

public interface MentoringSessionService {
    MentoringSessionResponse create(MentoringSessionCreateRequest request);

    MentoringSessionResponse get(Long sessionId);

    Page<MentoringSessionResponse> getByMentor(Long mentorId, Pageable pageable);

    Page<MentoringSessionResponse> getByMentee(Long menteeId, Pageable pageable);

    Page<MentoringSessionResponse> getByProgram(Long programId, Pageable pageable);
    
    List<MentoringSessionResponse> getByDepartment(Long departmentId);

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
