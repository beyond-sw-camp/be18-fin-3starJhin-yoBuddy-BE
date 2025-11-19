package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MentorTaskDetailResponse {

    private Long taskId;
    private String title;
    private String description;
    private LocalDate dueDate;

    private String status;
    private Integer grade;
    private LocalDateTime submittedAt;

    private List<SubmissionFileInfo> submissionFiles;

    private String feedback;

    private MenteeInfo mentee;

    private Long programId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @Getter
    @Builder
    public static class SubmissionFileInfo {
        private Long fileId;
        private String filename;
        private String fileUrl;
    }

    @Getter
    @Builder
    public static class MenteeInfo {
        private Long menteeId;
        private String menteeName;
    }
}
