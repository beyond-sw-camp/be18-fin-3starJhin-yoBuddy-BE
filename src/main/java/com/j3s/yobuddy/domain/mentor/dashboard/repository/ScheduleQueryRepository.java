package com.j3s.yobuddy.domain.mentor.dashboard.repository;

import com.j3s.yobuddy.domain.mentor.dashboard.response.ScheduleResponse;
import java.time.YearMonth;

public interface ScheduleQueryRepository {
    ScheduleResponse getMonthlySchedule(Long mentorId, YearMonth month);
}