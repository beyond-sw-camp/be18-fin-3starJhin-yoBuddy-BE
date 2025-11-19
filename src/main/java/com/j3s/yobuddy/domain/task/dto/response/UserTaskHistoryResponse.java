package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserTaskHistoryResponse {

    private Long taskId;
    private Long userId;

    private List<SubmissionInfo> submissions;

    @Getter
    @Builder
    public static class SubmissionInfo {
        private Long submissionId;
        private LocalDateTime submittedAt;
        private Integer grade;
        private String feedback;
        private String evaluator;
        private String status;
        private List<FileInfo> files;
    }

    @Getter
    @Builder
    public static class FileInfo {
        private Long fileId;
        private String filename;
        private String fileUrl;
    }
}
