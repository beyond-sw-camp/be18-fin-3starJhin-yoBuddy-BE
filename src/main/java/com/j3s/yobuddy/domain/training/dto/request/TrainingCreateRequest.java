package com.j3s.yobuddy.domain.training.dto.request;

import com.j3s.yobuddy.domain.training.entity.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TrainingCreateRequest {

    @NotBlank(message = "title is required")
    private String title;

    @NotNull(message = "type is required")
    private TrainingType type;  // ONLINE / OFFLINE

    @NotBlank(message = "description is required")
    private String description;

    private List<Long> fileIds;
}