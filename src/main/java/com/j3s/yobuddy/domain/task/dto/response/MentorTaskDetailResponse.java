package com.j3s.yobuddy.domain.task.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MentorTaskDetailResponse {

    private Long userTaskId;
    private Long menteeId;
    private String menteeName;

    private Long taskId;
    private String title;
    private String description;
    private Integer points;
    private LocalDate dueDate;

    private String status;
    private Integer grade;
    private LocalDateTime submittedAt;
    private String comment;
    private String feedback;

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
