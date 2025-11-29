package com.j3s.yobuddy.domain.user.dashboard.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserScheduleResponse {

    private List<ScheduleItem> schedules;

    public enum ScheduleType {
        MENTORING, TASK, TRAINING
    }

    @Getter
    @AllArgsConstructor
    public static class ScheduleItem {

        private ScheduleType type;
        private Long sessionId;
        private Long userTaskId;
        private Long userTrainingId;
        private String date;
        private String time;
        private String mentorName;
        private String status;
    }
}
