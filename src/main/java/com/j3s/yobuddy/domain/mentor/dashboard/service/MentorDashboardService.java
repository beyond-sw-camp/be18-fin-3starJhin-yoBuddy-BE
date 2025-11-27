package com.j3s.yobuddy.domain.mentor.dashboard.service;

import com.j3s.yobuddy.domain.mentor.dashboard.repository.MenteePerformanceQueryRepository;
import com.j3s.yobuddy.domain.mentor.dashboard.repository.MentorQueryRepository;
import com.j3s.yobuddy.domain.mentor.dashboard.repository.ScheduleQueryRepository;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MenteeListResponse;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MenteeOnboardingPerformanceResponse;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MentorSummaryResponse;
import com.j3s.yobuddy.domain.mentor.dashboard.response.ScheduleResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MentorDashboardService {

    private final MentorQueryRepository mentorQueryRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final MenteePerformanceQueryRepository menteePerformanceQueryRepository;

    public MentorSummaryResponse getSummary(Long mentorId) {
        return mentorQueryRepository.getMentorSummary(mentorId);
    }

    public MenteeListResponse getMentees(Long mentorId) {
        return mentorQueryRepository.getMentees(mentorId);
    }

    public ScheduleResponse getSchedule(Long mentorId, String month) {
        YearMonth ym = YearMonth.parse(month);
        return scheduleQueryRepository.getMonthlySchedule(mentorId, ym);
    }

    public MenteeOnboardingPerformanceResponse getMenteeOnboardingPerformance(
        Long mentorId,
        Long menteeId,
        LocalDate from,
        LocalDate to
    ) {
        return menteePerformanceQueryRepository.getMenteeOnboardingPerformance(
            mentorId, menteeId, from, to
        );
    }
}