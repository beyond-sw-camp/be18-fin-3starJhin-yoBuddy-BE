package com.j3s.yobuddy.domain.task.dto.response;

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

    private String fileName; // 파일 기능은 나중에
    private String fileUrl;  // 지금은 null 로 내려가기

    public static AdminTaskDetailResponse from(OnboardingTask onboardingTask) {
        return AdminTaskDetailResponse.builder()
            .taskId(onboardingTask.getId())
            .title(onboardingTask.getTitle())
            .description(onboardingTask.getDescription())
            .departmentIds(
                onboardingTask.getTaskDepartments().stream()
                    .map(td -> td.getDepartment().getDepartmentId())
                    .toList()
            )
            .createdAt(onboardingTask.getCreatedAt())
            .fileName(null)
            .fileUrl(null)
            .build();
    }
}
