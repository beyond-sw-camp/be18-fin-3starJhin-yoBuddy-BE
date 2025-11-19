package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram.ProgramStatus;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.training.dto.request.ProgramTrainingAssignRequest;
import com.j3s.yobuddy.domain.training.dto.request.TrainingCreateRequest;
import com.j3s.yobuddy.domain.training.dto.request.TrainingUpdateRequest;
import com.j3s.yobuddy.domain.training.dto.response.AssignedProgramResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingAssignResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingItemResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingUnassignResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingsResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDeleteResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingListItemResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingResponse;
import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import com.j3s.yobuddy.domain.training.entity.Training;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.exception.InvalidTrainingDataException;
import com.j3s.yobuddy.domain.training.exception.InvalidTrainingUpdateDataException;
import com.j3s.yobuddy.domain.training.exception.MissingOnlineUrlException;
import com.j3s.yobuddy.domain.training.exception.ProgramAlreadyCompletedException;
import com.j3s.yobuddy.domain.training.exception.TrainingInUseException;
import com.j3s.yobuddy.domain.training.exception.TrainingNotFoundException;
import com.j3s.yobuddy.domain.training.repository.ProgramTrainingQueryRepository;
import com.j3s.yobuddy.domain.training.repository.ProgramTrainingRepository;
import com.j3s.yobuddy.domain.training.repository.TrainingRepository;
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
    private final OnboardingProgramRepository onboardingProgramRepository;
    private final ProgramTrainingQueryRepository programTrainingQueryRepository;

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

        if (request.getType() == TrainingType.ONLINE && request.getOnlineUrl() == null){
            throw new InvalidTrainingDataException("(온라인 교육의 URL은 필수입니다.)");
        }

        // TODO: fileIds 로 Files 연동은 추후
        Training training = Training.create(
            request.getTitle(),
            request.getType(),
            request.getDescription(),
            request.getOnlineUrl()
        );

        Training saved = trainingRepository.save(training);

        return TrainingResponse.from(saved);
    }

    @Transactional
    public TrainingResponse updateTraining(Long trainingId, TrainingUpdateRequest request) {

        Training training = trainingRepository.findById(trainingId)
            .filter(t -> !t.isDeleted())
            .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        boolean hasUpdate =
            (request.getTitle() != null && !request.getTitle().isBlank()) ||
                request.getType() != null ||
                (request.getDescription() != null && !request.getDescription().isBlank()) ||
                (request.getOnlineUrl() != null && !request.getOnlineUrl().isBlank());

        if (!hasUpdate) {
            throw new InvalidTrainingUpdateDataException();
        }

        // --- 🔍 ONLINE URL 필수 검증 ---
        // 1) type 변경 요청이 들어와서 ONLINE으로 바뀌는 경우
        if (request.getType() == TrainingType.ONLINE) {
            if (request.getOnlineUrl() == null || request.getOnlineUrl().isBlank()) {
                throw new MissingOnlineUrlException(); // 새로 정의한 예외
            }
        }

        // 2) type 변경은 없지만 이미 ONLINE 상태라면, onlineUrl 수정 요청이 올 때 검증
        if (request.getType() == null && training.getType() == TrainingType.ONLINE) {
            if (request.getOnlineUrl() != null && request.getOnlineUrl().isBlank()) {
                throw new MissingOnlineUrlException();
            }
        }
        // --- END 검증 ---

        // 업데이트 로직
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            training.updateTitle(request.getTitle());
        }

        if (request.getType() != null) {
            training.updateType(request.getType());
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            training.updateDescription(request.getDescription());
        }

        if (request.getOnlineUrl() != null && !request.getOnlineUrl().isBlank()) {
            training.updateOnlineUrl(request.getOnlineUrl());
        }

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

    @Override
    @Transactional
    public ProgramTrainingAssignResponse assignTrainingToProgram(
        Long programId,
        Long trainingId,
        ProgramTrainingAssignRequest request
    ) {
        OnboardingProgram program = onboardingProgramRepository.findById(programId)
            .orElseThrow(() ->
                new InvalidTrainingDataException("프로그램을 찾을 수 없습니다. programId=" + programId));

        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() ->
                new TrainingNotFoundException(trainingId));

        boolean alreadyAssigned =
            programTrainingRepository.existsByProgram_ProgramIdAndTraining_TrainingId(programId, trainingId);

        if (alreadyAssigned) {
            throw new InvalidTrainingDataException(
                "이미 해당 프로그램에 연결된 교육입니다. programId=" + programId + ", trainingId=" + trainingId
            );
        }

        TrainingType trainingType = training.getType();
        if (trainingType == null) {
            throw new InvalidTrainingDataException(
                "교육 유형(type)이 설정되지 않은 교육입니다. trainingId=" + trainingId
            );
        }

        if (trainingType == TrainingType.ONLINE) {
            if (request == null
                || request.getStartDate() == null
                || request.getEndDate() == null) {
                throw new InvalidTrainingDataException(
                    "온라인 교육은 startDate와 endDate가 모두 필요합니다. trainingId=" + trainingId
                );
            }

            if (request.getStartDate().isAfter(request.getEndDate())) {
                throw new InvalidTrainingDataException(
                    "온라인 교육의 startDate는 endDate보다 이후일 수 없습니다. "
                        + "(startDate=" + request.getStartDate()
                        + ", endDate=" + request.getEndDate() + ")"
                );
            }
        }

        if (trainingType == TrainingType.OFFLINE) {
            if (request == null || request.getScheduledAt() == null) {
                throw new InvalidTrainingDataException(
                    "오프라인 교육은 scheduledAt이 필요합니다. trainingId=" + trainingId
                );
            }
        }

        LocalDateTime effectiveAssignedAt =
            (request != null && request.getAssignedAt() != null)
                ? request.getAssignedAt()
                : LocalDateTime.now();

        ProgramTraining programTraining = ProgramTraining.builder()
            .program(program)
            .training(training)
            .assignedAt(effectiveAssignedAt)
            .scheduledAt(request != null ? request.getScheduledAt() : null)
            .startDate(request != null ? request.getStartDate() : null)
            .endDate(request != null ? request.getEndDate() : null)
            .build();

        programTrainingRepository.save(programTraining);

        return new ProgramTrainingAssignResponse(
            program.getProgramId(),
            training.getTrainingId(),
            training.getTitle(),
            trainingType.name(),
            effectiveAssignedAt
        );
    }

    @Override
    @Transactional
    public ProgramTrainingUnassignResponse unassignTrainingFromProgram(
        Long programId,
        Long trainingId
    ) {
        OnboardingProgram program = onboardingProgramRepository.findById(programId)
            .orElseThrow(() ->
                new InvalidTrainingDataException("프로그램을 찾을 수 없습니다. programId=" + programId));

        if (program.getStatus() == ProgramStatus.COMPLETED) {
            throw new ProgramAlreadyCompletedException(programId);
        }

        ProgramTraining programTraining = programTrainingRepository
            .findByProgram_ProgramIdAndTraining_TrainingId(programId, trainingId)
            .orElseThrow(() ->
                new TrainingNotFoundException(trainingId));

        String title = programTraining.getTraining().getTitle();

        programTrainingRepository.delete(programTraining);

        LocalDateTime unassignedAt = LocalDateTime.now();

        return new ProgramTrainingUnassignResponse(
            programId,
            trainingId,
            title,
            unassignedAt
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramTrainingsResponse getProgramTrainings(
        Long programId,
        Boolean includeUnassigned,
        String type
    ) {
        OnboardingProgram program = onboardingProgramRepository.findById(programId)
            .orElseThrow(() ->
                new InvalidTrainingDataException("프로그램을 찾을 수 없습니다. programId=" + programId));

        TrainingType trainingType = null;
        if (type != null) {
            try {
                trainingType = TrainingType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidTrainingDataException("지원하지 않는 교육 유형입니다. type=" + type);
            }
        }

        boolean include = Boolean.TRUE.equals(includeUnassigned);

        List<ProgramTrainingItemResponse> trainings =
            programTrainingQueryRepository.findProgramTrainings(programId, trainingType, include);

        return new ProgramTrainingsResponse(
            program.getProgramId(),
            program.getName(),
            program.getDescription(),
            program.getStartDate(),
            program.getEndDate(),
            trainings,
            trainings.size()
        );
    }
}