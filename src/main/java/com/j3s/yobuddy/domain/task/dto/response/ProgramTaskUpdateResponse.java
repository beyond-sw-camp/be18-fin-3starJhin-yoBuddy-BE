package com.j3s.yobuddy.domain.task.dto.response;

import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProgramTaskUpdateResponse {

    private final Long programTaskId;
    private final Long programId;
    private final String programName;
    private final Long taskId;
    private final String taskName;
    private final LocalDateTime dueDate;
    private final LocalDateTime assignedAt;

    public static ProgramTaskUpdateResponse from(ProgramTask pt) {
        return ProgramTaskUpdateResponse.builder()
            .programTaskId(pt.getId())
            .programId(pt.getOnboardingProgram().getProgramId())
            .programName(pt.getOnboardingProgram().getName())
            .taskId(pt.getOnboardingTask().getId())
            .taskName(pt.getOnboardingTask().getTitle())
            .dueDate(pt.getDueDate())
            .assignedAt(pt.getAssignedAt())
            .build();
    }
}
