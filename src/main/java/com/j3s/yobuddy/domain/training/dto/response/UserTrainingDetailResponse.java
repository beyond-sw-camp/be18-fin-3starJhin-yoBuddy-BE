package com.j3s.yobuddy.domain.training.dto.response;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.formresult.entity.FormResultStatus;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserTrainingDetailResponse {

    private Long userId;
    private Long trainingId;

    private String title;
    private TrainingType type;
    private String description;
    private String onlineUrl;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime scheduledAt;

    private UserTrainingStatus status;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private BigDecimal score;
    private BigDecimal maxScore;
    private BigDecimal passingScore;
    private FormResultStatus result;
    private LocalDateTime submittedAt;

    private List<FileResponse> attachedFiles;

    public void setAttachedFiles(List<FileResponse> attachedFiles) {
        this.attachedFiles = attachedFiles;
    }
}
