package com.j3s.yobuddy.domain.user.dashboard.service;

import com.j3s.yobuddy.domain.user.dashboard.repository.UserPerformanceQueryRepository;
import com.j3s.yobuddy.domain.user.dashboard.repository.UserQueryRepository;
import com.j3s.yobuddy.domain.user.dashboard.repository.UserScheduleQueryRepository;
import com.j3s.yobuddy.domain.user.dashboard.response.MentorResponse;
import com.j3s.yobuddy.domain.user.dashboard.response.UserOnboardingPerformanceResponse;
import com.j3s.yobuddy.domain.user.dashboard.response.UserScheduleResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDashboardService {

    private final UserQueryRepository userQueryRepository;
    private final UserScheduleQueryRepository userScheduleQueryRepository;
    private final UserPerformanceQueryRepository userPerformanceQueryRepository;

    public MentorResponse getMentor(Long userId) {
        return userQueryRepository.getMentor(userId);
    }

    public UserScheduleResponse getSchdule(Long userId, String month) {
        YearMonth ym = YearMonth.parse(month);

        return userScheduleQueryRepository.getMonthlySchedule(userId, ym);
    }

    public UserScheduleResponse getWeeklySchedule(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDate start = now.with(DayOfWeek.MONDAY);
        LocalDate end = now.with(DayOfWeek.SUNDAY);

        return userScheduleQueryRepository.getWeeklySchedule(userId, start, end);
    }

    public UserOnboardingPerformanceResponse getOnboardingPerformance(Long userId, Long mentorId,
        LocalDate from, LocalDate to) {

        return userPerformanceQueryRepository.getOnboardingPerformance(userId, mentorId, from, to);
    }
}
