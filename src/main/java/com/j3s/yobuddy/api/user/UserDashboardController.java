package com.j3s.yobuddy.api.user;

import com.j3s.yobuddy.domain.user.dashboard.response.MentorResponse;
import com.j3s.yobuddy.domain.user.dashboard.response.UserOnboardingPerformanceResponse;
import com.j3s.yobuddy.domain.user.dashboard.response.UserScheduleResponse;
import com.j3s.yobuddy.domain.user.dashboard.service.UserDashboardService;
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
@RequestMapping("/api/v1/users")
public class UserDashboardController {

    private final UserDashboardService userDashboardService;

    @GetMapping("/{userId}/mentor")
    public MentorResponse getMentor(@PathVariable Long userId) {
        return userDashboardService.getMentor(userId);
    }

    @GetMapping("/{userId}/schedule")
    public UserScheduleResponse getSchedule(@PathVariable Long userId, @RequestParam String month) {
        return userDashboardService.getSchdule(userId, month);
    }

    @GetMapping("/{userId}/weekly")
    public UserScheduleResponse getWeeklySchedule(@PathVariable Long userId) {
        return userDashboardService.getWeeklySchedule(userId);
    }

    @GetMapping("/{userId}/mentors/{mentorId}/onboarding-performance")
    public UserOnboardingPerformanceResponse getOnboardingPerformance(@PathVariable Long userId,
        @PathVariable Long mentorId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return userDashboardService.getOnboardingPerformance(userId, mentorId, from, to);
    }

}
