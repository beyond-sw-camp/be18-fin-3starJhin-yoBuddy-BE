package com.j3s.yobuddy.domain.training.dto.request;

import com.j3s.yobuddy.domain.training.entity.TrainingType;
import java.util.List;
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
    private final String onlineUrl;
    private final List<Long> addFileIds;
    private final List<Long> removeFileIds;
}
