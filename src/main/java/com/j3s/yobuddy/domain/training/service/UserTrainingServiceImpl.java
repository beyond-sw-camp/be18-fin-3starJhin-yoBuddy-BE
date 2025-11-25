package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingItemResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingsResponse;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
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
    private final FileRepository fileRepository;

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
    public UserTrainingDetailResponse getUserTrainingDetail(Long pathUserId, Long trainingId) {

        UserTrainingDetailResponse dto =
            userTrainingQueryRepository.findUserTrainingDetail(pathUserId, trainingId)
                .orElseThrow(() -> new UserTrainingsNotFoundException(pathUserId));

        List<FileResponse> files =
            fileRepository.findByRefTypeAndRefId(RefType.TRAINING, trainingId).stream()
                .map(FileResponse::from)
                .toList();

        dto.setAttachedFiles(files);

        return dto;
    }
}
