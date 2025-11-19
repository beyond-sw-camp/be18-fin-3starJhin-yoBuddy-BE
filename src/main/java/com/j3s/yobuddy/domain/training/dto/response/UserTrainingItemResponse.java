package com.j3s.yobuddy.domain.training.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserTrainingItemResponse {

    private final Long trainingId;
    private final String title;
    private final String type;
    private final String onlineUrl;

    private final String status;
    private final BigDecimal score;
    private final LocalDateTime completedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private final LocalDateTime scheduledAt;
    private final LocalDate startDate;
    private final LocalDate endDate;
}
