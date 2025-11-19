package com.j3s.yobuddy.domain.training.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProgramTrainingsResponse {

    private final Long programId;
    private final String programName;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private final List<ProgramTrainingItemResponse> trainings;
    private final int totalCount;
}
