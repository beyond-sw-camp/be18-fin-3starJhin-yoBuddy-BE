package com.j3s.yobuddy.domain.training.dto.response;

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
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final List<AssignedProgramResponse> assignedPrograms;
    private final List<TrainingFileResponse> attachedFiles;

    public static TrainingDetailResponse of(
        Training training,
        List<AssignedProgramResponse> assignedPrograms
    ) {
        return TrainingDetailResponse.builder()
            .trainingId(training.getTrainingId())
            .title(training.getTitle())
            .type(training.getType())
            .description(training.getDescription())
            .createdAt(training.getCreatedAt())
            .updatedAt(training.getUpdatedAt())
            .assignedPrograms(assignedPrograms)
            .attachedFiles(List.of())
            .build();
    }
}
