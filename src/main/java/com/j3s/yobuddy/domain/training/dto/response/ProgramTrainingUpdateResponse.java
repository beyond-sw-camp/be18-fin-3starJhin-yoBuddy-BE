package com.j3s.yobuddy.domain.training.dto.response;

import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProgramTrainingUpdateResponse {

    private final Long programTrainingId;
    private final Long programId;
    private final String programName;
    private final Long trainingId;
    private final String trainingTitle;
    private final String trainingType;
    private final LocalDateTime scheduledAt;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDateTime assignedAt;

    public static ProgramTrainingUpdateResponse from(ProgramTraining pt) {
        return ProgramTrainingUpdateResponse.builder()
            .programTrainingId(pt.getProgramTrainingId())
            .programId(pt.getProgram().getProgramId())
            .programName(pt.getProgram().getName())
            .trainingId(pt.getTraining().getTrainingId())
            .trainingTitle(pt.getTraining().getTitle())
            .trainingType(pt.getTraining().getType().name())  // 예: ONLINE/OFFLINE
            .scheduledAt(pt.getScheduledAt())
            .startDate(pt.getStartDate())
            .endDate(pt.getEndDate())
            .assignedAt(pt.getAssignedAt())
            .build();
    }

}
