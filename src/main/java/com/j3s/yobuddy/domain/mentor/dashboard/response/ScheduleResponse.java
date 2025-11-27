package com.j3s.yobuddy.domain.mentor.dashboard.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleResponse {

    private List<ScheduleItem> schedules;

    @Getter
    @AllArgsConstructor
    public static class ScheduleItem {
        private Long sessionId;
        private String date;
        private String time;
        private String menteeName;
        private String status;
    }
}
