package com.j3s.yobuddy.domain.training.dto.response;

import com.j3s.yobuddy.domain.training.entity.FormResultStatus;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserTrainingDetailResponse {

    private Long userId;
    private Long trainingId;

    private String title;
    private TrainingType type;
    private String description;
    private String onlineUrl;

    // 일정 (Program_Trainings)
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime scheduledAt;

    // User_Trainings
    private UserTrainingStatus status;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Form_Results
    private BigDecimal score;
    private BigDecimal maxScore;
    private BigDecimal passingScore;
    private FormResultStatus result;   // PASS / FAIL
    private LocalDateTime submittedAt;
}