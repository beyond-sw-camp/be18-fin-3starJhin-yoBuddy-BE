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
public class TrainingDetailResponse {

    private final Long trainingId;
    private final String title;
    private final TrainingType type;
    private final String description;
    private final String onlineUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<AssignedProgramResponse> assignedPrograms;

    private final List<FileResponse> attachedFiles;

    public static TrainingDetailResponse of(
        Training training,
        List<AssignedProgramResponse> assignedPrograms,
        List<FileResponse> attachedFiles
    ) {

        return TrainingDetailResponse.builder()
            .trainingId(training.getTrainingId())
            .title(training.getTitle())
            .type(training.getType())
            .description(training.getDescription())
            .onlineUrl(training.getOnlineUrl())
            .createdAt(training.getCreatedAt())
            .updatedAt(training.getUpdatedAt())
            .assignedPrograms(assignedPrograms)
            .attachedFiles(attachedFiles)
            .build();
    }
}
