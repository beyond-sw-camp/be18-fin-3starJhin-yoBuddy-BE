package com.j3s.yobuddy.domain.training.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProgramTrainingItemResponse {

    private final Long trainingId;
    private final String title;
    private final String type;
    private final String description;
    private String onlineUrl;

    private final LocalDateTime assignedAt;
    private final LocalDateTime scheduledAt;
    private final LocalDate startDate;
    private final LocalDate endDate;
}
