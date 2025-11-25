package com.j3s.yobuddy.domain.training.dto.response;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.training.entity.Training;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainingResponse {

    private Long trainingId;
    private String title;
    private TrainingType type;
    private String description;
    private String onlineUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;

    private List<FileResponse> attachedFiles;

    public static TrainingResponse of(Training training, List<FileResponse> files) {
        return TrainingResponse.builder()
            .trainingId(training.getTrainingId())
            .title(training.getTitle())
            .type(training.getType())
            .description(training.getDescription())
            .onlineUrl(training.getOnlineUrl())
            .createdAt(training.getCreatedAt())
            .updatedAt(training.getUpdatedAt())
            .isDeleted(training.isDeleted())
            .attachedFiles(files)
            .build();
    }
}

