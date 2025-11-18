package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.domain.training.dto.request.TrainingCreateRequest;
import com.j3s.yobuddy.domain.training.dto.request.TrainingUpdateRequest;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDeleteResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingListItemResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingResponse;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrainingAdminService {
    public Page<TrainingListItemResponse> getTrainingList( TrainingType type, Long programId, String keyword, Pageable pageable);
    public TrainingDetailResponse getTrainingDetail(Long trainingId);
    public TrainingResponse createTraining(TrainingCreateRequest request);
    public TrainingResponse updateTraining(Long trainingId, TrainingUpdateRequest request);
    public TrainingDeleteResponse deleteTraining(Long trainingId);
}
