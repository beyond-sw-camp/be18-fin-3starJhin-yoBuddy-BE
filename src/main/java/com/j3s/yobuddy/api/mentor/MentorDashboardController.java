package com.j3s.yobuddy.api.mentor;

import com.j3s.yobuddy.domain.mentor.dashboard.response.MenteeListResponse;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MenteeOnboardingPerformanceResponse;
import com.j3s.yobuddy.domain.mentor.dashboard.response.MentorSummaryResponse;
import com.j3s.yobuddy.domain.mentor.dashboard.response.ScheduleResponse;
import com.j3s.yobuddy.domain.mentor.dashboard.service.MentorDashboardService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentors")
public class MentorDashboardController {

    private final MentorDashboardService mentorDashboardService;

    @GetMapping("/{mentorId}/summary")
    public MentorSummaryResponse getSummary(@PathVariable Long mentorId) {
        return mentorDashboardService.getSummary(mentorId);
    }

    @GetMapping("/{mentorId}/mentees")
    public MenteeListResponse getMentees(@PathVariable Long mentorId) {
        return mentorDashboardService.getMentees(mentorId);
    }

    @GetMapping("/{mentorId}/schedule")
    public ScheduleResponse getSchedule(
        @PathVariable Long mentorId,
        @RequestParam String month
    ) {
        return mentorDashboardService.getSchedule(mentorId, month);
    }

    @GetMapping("/{mentorId}/mentees/{menteeId}/onboarding-performance")
    public MenteeOnboardingPerformanceResponse getMenteeOnboardingPerformance(
        @PathVariable Long mentorId,
        @PathVariable Long menteeId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return mentorDashboardService.getMenteeOnboardingPerformance(mentorId, menteeId, from, to);
    }
}
