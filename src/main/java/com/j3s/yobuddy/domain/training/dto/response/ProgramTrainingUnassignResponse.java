package com.j3s.yobuddy.domain.training.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProgramTrainingUnassignResponse {

    private final Long programId;
    private final Long trainingId;
    private final String title;
    private final LocalDateTime unassignedAt;
}
