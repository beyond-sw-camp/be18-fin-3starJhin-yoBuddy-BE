package com.j3s.yobuddy.domain.mentoring.service;

import com.j3s.yobuddy.domain.mentoring.dto.request.MentoringSessionCreateRequest;
import com.j3s.yobuddy.domain.mentoring.dto.request.MentoringSessionUpdateRequest;
import com.j3s.yobuddy.domain.mentoring.dto.response.MentoringSessionResponse;
import java.util.List;

public interface MentoringSessionService {
    MentoringSessionResponse create(MentoringSessionCreateRequest request);

    MentoringSessionResponse get(Long sessionId);

    List<MentoringSessionResponse> getAll();

    List<MentoringSessionResponse> getByMentor(Long mentorId);

    List<MentoringSessionResponse> getByMentee(Long menteeId);

    List<MentoringSessionResponse> getByProgram(Long programId);

    MentoringSessionResponse update(Long sessionId, MentoringSessionUpdateRequest request);

    void delete(Long sessionId);
}
