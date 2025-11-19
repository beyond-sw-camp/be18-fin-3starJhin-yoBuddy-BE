package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.domain.training.dto.response.UserTrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingsResponse;

public interface UserTrainingService {
    UserTrainingsResponse getUserTrainings(Long userId, String status, String type);
    UserTrainingDetailResponse getUserTrainingDetail(Long pathUserId, Long trainingId);
}
