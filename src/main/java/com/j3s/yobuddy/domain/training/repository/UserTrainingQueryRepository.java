package com.j3s.yobuddy.domain.training.repository;

import com.j3s.yobuddy.domain.training.dto.response.UserTrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingItemResponse;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.UserTraining;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTrainingQueryRepository {

    List<UserTrainingItemResponse> findUserTrainings(
        Long userId,
        UserTrainingStatus status,
        TrainingType type
    );

    Optional<UserTrainingDetailResponse> findUserTrainingDetail(Long userId, Long trainingId);

    Optional<UserTraining> findEntity(Long userId, Long trainingId);

    List<UserTraining> findOverdueTrainings(LocalDate today);

    List<UserTraining> findOnlineDueAt(LocalDate targetDate);

    List<UserTraining> findOfflineScheduledAt(LocalDate targetDate);

    List<UserTraining> findOfflineFormPendingAt(LocalDate targetDate);
}
