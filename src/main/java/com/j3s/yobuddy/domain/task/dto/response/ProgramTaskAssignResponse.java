package com.j3s.yobuddy.domain.task.dto.response;

import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProgramTaskAssignResponse {

    private Long programId;
    private Long taskId;
    private String title;
    private LocalDateTime assignedAt;
    private LocalDateTime dueDate;

    public static ProgramTaskAssignResponse of(ProgramTask programTask) {
        return ProgramTaskAssignResponse.builder()
            .programId(programTask.getOnboardingProgram().getProgramId())
            .taskId(programTask.getOnboardingTask().getId())
            .title(programTask.getOnboardingTask().getTitle())
            .assignedAt(programTask.getAssignedAt())
            .dueDate(programTask.getDueDate())
            .build();
    }
}
