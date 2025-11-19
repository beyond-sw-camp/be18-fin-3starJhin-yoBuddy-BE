package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class TaskDetailResponse {

    private Long taskId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer points;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<FileInfo> attachedFiles;
    private List<AssignedProgramInfo> assignedPrograms;

    @Getter
    @Builder
    public static class FileInfo {
        private Long fileId;
        private String filename;
        private String filepath;
    }

    @Getter
    @Builder
    public static class AssignedProgramInfo {
        private Long programId;
        private String programName;
        private LocalDateTime assignedAt;
    }
}
