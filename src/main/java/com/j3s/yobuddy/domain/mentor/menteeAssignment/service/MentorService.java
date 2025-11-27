package com.j3s.yobuddy.domain.mentor.menteeAssignment.service;

import com.j3s.yobuddy.domain.mentor.menteeAssignment.dto.request.AssignMenteeRequest;
import com.j3s.yobuddy.domain.mentor.menteeAssignment.dto.response.MenteeCandidateResponse;
import com.j3s.yobuddy.domain.mentor.menteeAssignment.dto.response.MenteeDetailResponse;
import com.j3s.yobuddy.domain.mentor.menteeAssignment.dto.response.MenteeListResponse;
import java.util.List;

public interface MentorService {

    void assignMentee(Long mentorId, AssignMenteeRequest request);

    void removeMentee(Long mentorId, Long menteeId);

    List<MenteeListResponse> getMentees(Long mentorId);

    List<MenteeCandidateResponse> getDepartmentNewbies(Long mentorId);

    MenteeDetailResponse getMenteeDetail(Long mentorId, Long menteeId);
}