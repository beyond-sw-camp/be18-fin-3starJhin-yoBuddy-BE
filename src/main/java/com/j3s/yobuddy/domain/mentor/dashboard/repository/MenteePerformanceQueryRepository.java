package com.j3s.yobuddy.domain.mentor.dashboard.repository;

import com.j3s.yobuddy.domain.mentor.dashboard.response.MenteeOnboardingPerformanceResponse;
import java.time.LocalDate;

public interface MenteePerformanceQueryRepository {
    MenteeOnboardingPerformanceResponse getMenteeOnboardingPerformance(
        Long mentorId,
        Long menteeId,
        LocalDate from,
        LocalDate to
    );
}
