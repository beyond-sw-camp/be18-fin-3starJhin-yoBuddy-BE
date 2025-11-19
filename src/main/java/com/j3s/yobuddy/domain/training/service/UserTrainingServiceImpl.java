package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.common.exception.BusinessException;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingItemResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingsResponse;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.j3s.yobuddy.domain.training.exception.ForbiddenOperationException;
import com.j3s.yobuddy.domain.training.exception.InvalidTrainingDataException;
import com.j3s.yobuddy.domain.training.exception.UserTrainingsNotFoundException;
import com.j3s.yobuddy.domain.training.repository.UserTrainingQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserTrainingServiceImpl implements UserTrainingService {

    private final UserTrainingQueryRepository userTrainingQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public UserTrainingsResponse getUserTrainings(Long userId, String status, String type) {

        UserTrainingStatus userTrainingStatus = null;
        if (status != null) {
            try {
                userTrainingStatus = UserTrainingStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidTrainingDataException(
                    "지원하지 않는 교육 진행 상태입니다. status=" + status
                );
            }
        }

        TrainingType trainingType = null;
        if (type != null) {
            try {
                trainingType = TrainingType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidTrainingDataException(
                    "지원하지 않는 교육 유형입니다. type=" + type
                );
            }
        }

        List<UserTrainingItemResponse> trainings =
            userTrainingQueryRepository.findUserTrainings(userId, userTrainingStatus, trainingType);

        if (trainings.isEmpty()) {
            throw new UserTrainingsNotFoundException(userId);
        }

        return new UserTrainingsResponse(
            userId,
            trainings,
            trainings.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserTrainingDetailResponse getUserTrainingDetail(Long pathUserId,
        Long trainingId) {

        // 2) 이 유저에게 할당된 이 training 이 없으면 → 404
        return userTrainingQueryRepository.findUserTrainingDetail(pathUserId, trainingId)
            .orElseThrow(() ->
                new UserTrainingsNotFoundException(pathUserId)
            );
    }
}
