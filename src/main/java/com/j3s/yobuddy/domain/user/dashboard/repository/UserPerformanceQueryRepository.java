package com.j3s.yobuddy.domain.user.dashboard.repository;

import com.j3s.yobuddy.domain.user.dashboard.response.UserOnboardingPerformanceResponse;
import java.time.LocalDate;

public interface UserPerformanceQueryRepository {

    UserOnboardingPerformanceResponse getOnboardingPerformance(Long userId, Long mentorId,
        LocalDate from, LocalDate to);

}
