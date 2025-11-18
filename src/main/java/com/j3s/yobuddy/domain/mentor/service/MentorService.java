package com.j3s.yobuddy.domain.mentor.service;

import com.j3s.yobuddy.domain.mentor.dto.request.AssignMenteeRequest;
import com.j3s.yobuddy.domain.mentor.dto.response.MenteeCandidateResponse;
import com.j3s.yobuddy.domain.mentor.dto.response.MenteeDetailResponse;
import com.j3s.yobuddy.domain.mentor.dto.response.MenteeListResponse;
import java.util.List;

public interface MentorService {

    void assignMentee(Long mentorId, AssignMenteeRequest request);

    void removeMentee(Long mentorId, Long menteeId);

    List<MenteeListResponse> getMentees(Long mentorId);

    List<MenteeCandidateResponse> getDepartmentNewbies(Long mentorId);

    MenteeDetailResponse getMenteeDetail(Long mentorId, Long menteeId);
}