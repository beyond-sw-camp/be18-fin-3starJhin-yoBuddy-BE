package com.j3s.yobuddy.domain.training.dto.request;

import com.j3s.yobuddy.domain.training.entity.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TrainingUpdateRequest {

    private final String title;
    private final TrainingType type;    // ONLINE / OFFLINE
    private final String description;
}
