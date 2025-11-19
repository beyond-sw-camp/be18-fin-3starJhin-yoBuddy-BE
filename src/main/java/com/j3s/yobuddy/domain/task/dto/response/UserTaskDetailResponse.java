package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserTaskDetailResponse {

    private Long taskId;
    private String title;
    private String description;

    private LocalDate dueDate;
    private Integer points;

    private String status;          // PENDING, SUBMITTED, GRADED, LATE
    private Integer grade;
    private LocalDateTime submittedAt;

    private String feedback;

    private List<FileInfo> files;
    private List<CommentInfo> comments;

    private LocalDateTime updatedAt;


    @Getter
    @Builder
    public static class FileInfo {
        private Long fileId;
        private String fileName;
        private String filePath;
    }

    @Getter
    @Builder
    public static class CommentInfo {
        private Long commentId;
        private String userName;
        private String comment;
        private LocalDateTime createdAt;
    }
}
