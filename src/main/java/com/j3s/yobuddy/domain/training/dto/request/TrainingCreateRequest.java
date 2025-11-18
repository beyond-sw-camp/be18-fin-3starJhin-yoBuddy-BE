package com.j3s.yobuddy.domain.training.dto.request;

import com.j3s.yobuddy.domain.training.entity.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrainingCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private final String title;

    @NotNull(message = "교육 유형은 필수입니다.")
    private final TrainingType type;  // ONLINE / OFFLINE

    @NotBlank(message = "교육 설명은 필수입니다.")
    private final String description;

    private final List<Long> fileIds;
}