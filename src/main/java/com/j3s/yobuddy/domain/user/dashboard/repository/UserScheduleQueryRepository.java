package com.j3s.yobuddy.domain.user.dashboard.repository;

import com.j3s.yobuddy.domain.user.dashboard.response.UserScheduleResponse;
import java.time.LocalDate;
import java.time.YearMonth;

public interface UserScheduleQueryRepository {

    UserScheduleResponse getMonthlySchedule(Long userId, YearMonth month);

    UserScheduleResponse getWeeklySchedule(Long userId, LocalDate start, LocalDate end);
}
