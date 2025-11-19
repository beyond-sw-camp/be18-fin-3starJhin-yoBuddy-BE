package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TaskSubmissionHistoryResponse {

    private Long userId;
    private Long taskId;

    private List<SubmissionEntry> submissions;

    @Getter
    @Builder
    public static class SubmissionEntry {

        private Long submissionId;

        private LocalDateTime submittedAt;
        private Integer grade;
        private String feedback;

        private List<FileInfo> files;

        private String status;         // SUBMITTED, GRADED, RESUBMITTED
        private String evaluator;      // 평가자 이름

        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    public static class FileInfo {
        private Long fileId;
        private String fileName;
    }
}
