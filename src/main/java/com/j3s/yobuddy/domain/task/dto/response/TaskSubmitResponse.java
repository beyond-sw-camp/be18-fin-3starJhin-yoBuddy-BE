package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TaskSubmitResponse {

    private Long userId;
    private Long taskId;

    private String status;              // SUBMITTED
    private LocalDateTime submittedAt;

    private List<FileInfo> files;       // 제출된 파일 리스트

    private String comment;

    @Getter
    @Builder
    public static class FileInfo {
        private Long fileId;
        private String fileName;
    }
}
