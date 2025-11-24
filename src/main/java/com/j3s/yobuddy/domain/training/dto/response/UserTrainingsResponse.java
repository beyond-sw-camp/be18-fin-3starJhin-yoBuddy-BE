package com.j3s.yobuddy.domain.training.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserTrainingsResponse {

    private final Long userId;
    private final List<UserTrainingItemResponse> trainings;
    private final int totalCount;
}
