package com.j3s.yobuddy.domain.task.dto.response;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.task.entity.OnboardingTask;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class TaskUpdateResponse {
    private Long taskId;
    private String title;
    private String description;
    private Integer points;
    private List<Long> departmentIds;
    private List<FileResponse> files;

    public static TaskUpdateResponse of(OnboardingTask task, List<FileResponse> files) {
        return TaskUpdateResponse.builder()
            .taskId(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .points(task.getPoints())
            .departmentIds(
                task.getTaskDepartments().stream()
                    .map(td -> td.getDepartment().getDepartmentId())
                    .toList()
            )
            .files(files)
            .build();
    }
}
