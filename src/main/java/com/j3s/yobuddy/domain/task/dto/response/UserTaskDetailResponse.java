package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserTaskDetailResponse {

    private Long userTaskId;
    private Long taskId;
    private String title;
    private String description;

    private LocalDate dueDate;
    private Integer points;

    private String status;
    private Integer grade;
    private LocalDateTime submittedAt;
    private String feedback;
    private String comment;
    private List<FileInfo> taskFiles;
    private List<FileInfo> submittedFiles;

    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class FileInfo {
        private Long fileId;
        private String fileName;
        private String filePath;
    }
}