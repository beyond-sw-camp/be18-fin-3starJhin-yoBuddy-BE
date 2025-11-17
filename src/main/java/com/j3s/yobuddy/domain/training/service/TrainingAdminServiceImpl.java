package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.domain.training.dto.request.TrainingCreateRequest;
import com.j3s.yobuddy.domain.training.dto.request.TrainingUpdateRequest;
import com.j3s.yobuddy.domain.training.dto.response.AssignedProgramResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDeleteResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingListItemResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingResponse;
import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import com.j3s.yobuddy.domain.training.entity.Training;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.exception.InvalidTrainingDataException;
import com.j3s.yobuddy.domain.training.exception.InvalidTrainingUpdateDataException;
import com.j3s.yobuddy.domain.training.exception.TrainingInUseException;
import com.j3s.yobuddy.domain.training.exception.TrainingNotFoundException;
import com.j3s.yobuddy.domain.training.repository.ProgramTrainingRepository;
import com.j3s.yobuddy.domain.training.repository.TrainingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrainingAdminServiceImpl implements TrainingAdminService {

    private final TrainingRepository trainingRepository;
    private final ProgramTrainingRepository programTrainingRepository;

    @Transactional(readOnly = true)
    public Page<TrainingListItemResponse> getTrainingList(
        TrainingType type,
        Long programId,
        String keyword,
        Pageable pageable
    ) {
        Page<Training> trainingPage =
            trainingRepository.searchTrainings(type, programId, keyword, pageable);

        List<Training> trainings = trainingPage.getContent();
        if (trainings.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, trainingPage.getTotalElements());
        }

        List<Long> trainingIds = trainings.stream()
            .map(Training::getTrainingId)
            .toList();

        List<ProgramTraining> mappings =
            programTrainingRepository.findByTraining_TrainingIdIn(trainingIds);

        Map<Long, List<AssignedProgramResponse>> programMap = mappings.stream()
            .filter(pt -> pt.getProgram() != null &&
                (!pt.getProgram().isDeleted()))
            .collect(Collectors.groupingBy(
                pt -> pt.getTraining().getTrainingId(),
                Collectors.mapping(AssignedProgramResponse::from, Collectors.toList())
            ));

        List<TrainingListItemResponse> responseList = trainings.stream()
            .map(t -> TrainingListItemResponse.of(
                t,
                programMap.getOrDefault(t.getTrainingId(), List.of())
            ))
            .toList();

        return new PageImpl<>(responseList, pageable, trainingPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public TrainingDetailResponse getTrainingDetail(Long trainingId) {

        Training training = trainingRepository.findById(trainingId)
            .filter(t -> !t.isDeleted())
            .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        List<AssignedProgramResponse> assignedPrograms =
            programTrainingRepository.findByTraining_TrainingId(trainingId).stream()
                .filter(pt -> pt.getProgram() != null && !pt.getProgram().isDeleted())
                .map(AssignedProgramResponse::from)
                .toList();

        return TrainingDetailResponse.of(training, assignedPrograms);
    }

    @Transactional
    public TrainingResponse createTraining(TrainingCreateRequest request) {
        // 간단한 유효성 검증 → 400 INVALID_TRAINING_DATA 용
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new InvalidTrainingDataException("(title 은 필수입니다.)");
        }
        if (request.getType() == null) {
            throw new InvalidTrainingDataException("(type 은 필수입니다.)");
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new InvalidTrainingDataException("(description 은 필수입니다.)");
        }

        // TODO: fileIds 로 Files 연동은 추후
        Training training = Training.create(
            request.getTitle(),
            request.getType(),
            request.getDescription()
        );

        Training saved = trainingRepository.save(training);

        return TrainingResponse.from(saved);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public TrainingResponse updateTraining(Long trainingId, TrainingUpdateRequest request) {

        Training training = trainingRepository.findById(trainingId)
            .filter(t -> !t.isDeleted())
            .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        boolean hasUpdate =
            (request.getTitle() != null && !request.getTitle().isBlank()) ||
                request.getType() != null ||
                (request.getDescription() != null && !request.getDescription().isBlank());

        if (!hasUpdate) {
            throw new InvalidTrainingUpdateDataException();
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            training.updateTitle(request.getTitle());
        }

        if (request.getType() != null) {
            training.updateType(request.getType());
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            training.updateDescription(request.getDescription());
        }

        // TODO: fileIds 로 Files 연동은 추후

        entityManager.flush();
        entityManager.refresh(training);

        return TrainingResponse.from(training);
    }

    @Transactional
    public TrainingDeleteResponse deleteTraining(Long trainingId) {

        Training training = trainingRepository.findById(trainingId)
            .filter(t -> !t.isDeleted())
            .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        long activeProgramCount =
            programTrainingRepository
                .countByTraining_TrainingIdAndProgram_DeletedFalse(trainingId);

        if (activeProgramCount > 0) {
            throw new TrainingInUseException(trainingId, activeProgramCount);
        }

        LocalDateTime deletedAt = LocalDateTime.now();
        training.markDeleted(deletedAt);
        trainingRepository.save(training);

        return TrainingDeleteResponse.of(training.getTrainingId(), deletedAt);
    }
}