package com.j3s.yobuddy.domain.task.dto.response;

import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.task.entity.OnboardingTask;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminTaskDetailResponse {

    private Long taskId;
    private String title;
    private String description;
    private List<Long> departmentIds;
    private LocalDateTime createdAt;

    private List<FileResponse> attachedFiles;

    private String fileName;
    private String fileUrl;

    public static AdminTaskDetailResponse of(OnboardingTask task, List<FileResponse> files) {

        FileResponse first = (files == null || files.isEmpty()) ? null : files.get(0);

        return AdminTaskDetailResponse.builder()
            .taskId(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .departmentIds(
                task.getTaskDepartments().stream()
                    .map(td -> td.getDepartment().getDepartmentId())
                    .toList()
            )
            .createdAt(task.getCreatedAt())
            .attachedFiles(files)

            .fileName(first != null ? first.getFilename() /* or getFileName() */ : null)
            .fileUrl(first != null ? first.getUrl()      /* or getDownloadUrl()/getFileUrl() */ : null)

            .build();
    }
}
