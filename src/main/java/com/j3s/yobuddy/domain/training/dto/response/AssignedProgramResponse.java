package com.j3s.yobuddy.domain.training.dto.response;

import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssignedProgramResponse {

    private final Long programId;
    private final String programName;
    private final LocalDateTime assignedAt;

    public static AssignedProgramResponse from(ProgramTraining pt) {
        return AssignedProgramResponse.builder()
            .programId(pt.getProgram().getProgramId())
            .programName(pt.getProgram().getName())
            .assignedAt(pt.getAssignedAt())
            .build();
    }
}
