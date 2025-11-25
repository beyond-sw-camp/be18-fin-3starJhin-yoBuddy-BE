package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserTaskListResponse {

    private Long userId;
    private Long programId;
    private List<TaskInfo> tasks;

    @Getter
    @Builder
    public static class TaskInfo {

        private Long taskId;
        private String title;
        private LocalDate dueDate;

        // ENUM 문자열 그대로 내려가기 때문에 String 사용
        private String status;

        private Integer grade;
        private LocalDateTime submittedAt;
        private String feedback;
    }
}
