package com.j3s.yobuddy.domain.training.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class TrainingDeleteResponse {

    private final Long trainingId;
    private final LocalDateTime deletedAt;
}
