package com.j3s.yobuddy.domain.mentor.dashboard.repository;

import com.j3s.yobuddy.domain.mentor.dashboard.response.MenteeListResponse;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MentorSummaryResponse;

public interface MentorQueryRepository {
    MentorSummaryResponse getMentorSummary(Long mentorId);
    MenteeListResponse getMentees(Long mentorId);
}
