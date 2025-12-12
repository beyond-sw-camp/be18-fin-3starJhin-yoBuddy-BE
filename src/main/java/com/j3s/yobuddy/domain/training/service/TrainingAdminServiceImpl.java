package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.file.service.FileService;
import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram;
import com.j3s.yobuddy.domain.onboarding.entity.OnboardingProgram.ProgramStatus;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment.EnrollmentStatus;
import com.j3s.yobuddy.domain.programenrollment.repository.ProgramEnrollmentRepository;
import com.j3s.yobuddy.domain.training.dto.request.ProgramTrainingAssignRequest;
import com.j3s.yobuddy.domain.training.dto.request.ProgramTrainingUpdateRequest;
import com.j3s.yobuddy.domain.training.dto.response.AssignedProgramResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingAssignResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingItemResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingUnassignResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingUpdateResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingsResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDeleteResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingListItemResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingResponse;
import com.j3s.yobuddy.domain.training.entity.ProgramTraining;
import com.j3s.yobuddy.domain.training.entity.Training;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.exception.InvalidTrainingDataException;
import com.j3s.yobuddy.domain.training.exception.ProgramAlreadyCompletedException;
import com.j3s.yobuddy.domain.training.exception.TrainingInUseException;
import com.j3s.yobuddy.domain.training.exception.TrainingNotFoundException;
import com.j3s.yobuddy.domain.training.repository.ProgramTrainingQueryRepository;
import com.j3s.yobuddy.domain.training.repository.ProgramTrainingRepository;
import com.j3s.yobuddy.domain.training.repository.TrainingRepository;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TrainingAdminServiceImpl implements TrainingAdminService {

    private final TrainingRepository trainingRepository;
    private final ProgramTrainingRepository programTrainingRepository;
    private final OnboardingProgramRepository onboardingProgramRepository;
    private final ProgramTrainingQueryRepository programTrainingQueryRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;
    private final ProgramEnrollmentRepository programEnrollmentRepository;
    private final UserTrainingAssignmentService userTrainingAssignmentService;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public Page<TrainingListItemResponse> getTrainingList(
        TrainingType type,
        Long programId,
        String keyword,
        Pageable pageable) {
        Page<Training> trainingPage = trainingRepository.searchTrainings(type, programId, keyword,
            pageable);

        List<Training> trainings = trainingPage.getContent();
        if (trainings.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, trainingPage.getTotalElements());
        }

        List<Long> trainingIds = trainings.stream()
            .map(Training::getTrainingId)
            .toList();

        List<ProgramTraining> mappings = programTrainingRepository.findByTraining_TrainingIdIn(
            trainingIds);

        Map<Long, List<AssignedProgramResponse>> programMap = mappings.stream()
            .filter(pt -> pt.getProgram() != null && (!pt.getProgram().isDeleted()))
            .collect(Collectors.groupingBy(
                pt -> pt.getTraining().getTrainingId(),
                Collectors.mapping(AssignedProgramResponse::from, Collectors.toList())));

        List<TrainingListItemResponse> responseList = trainings.stream()
            .map(t -> TrainingListItemResponse.of(
                t,
                programMap.getOrDefault(t.getTrainingId(), List.of())))
            .toList();

        return new PageImpl<>(responseList, pageable, trainingPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public TrainingDetailResponse getTrainingDetail(Long trainingId) {

        Training training = trainingRepository.findById(trainingId)
            .filter(t -> !t.isDeleted())
            .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        List<AssignedProgramResponse> assignedPrograms = programTrainingRepository.findByTraining_TrainingId(
                trainingId)
            .stream()
            .filter(pt -> pt.getProgram() != null && !pt.getProgram().isDeleted())
            .map(AssignedProgramResponse::from)
            .toList();

        List<FileResponse> files = fileRepository.findByRefTypeAndRefId(RefType.TRAINING,
                trainingId).stream()
            .map(FileResponse::from)
            .toList();

        return TrainingDetailResponse.of(training, assignedPrograms, files);
    }

    @Transactional
    public TrainingResponse createTrainingWithFiles(
        String title,
        TrainingType type,
        String description,
        String onlineUrl,
        List<MultipartFile> files) throws Exception {

        Training training = Training.create(
            title, type, description, onlineUrl);
        Training saved = trainingRepository.save(training);

        // 새 파일 업로드
        if (files != null) {
            for (MultipartFile file : files) {
                FileEntity uploaded = fileService.uploadTempFile(file, FileType.TRAINING);
                fileService.bindFile(uploaded.getFileId(), RefType.TRAINING, saved.getTrainingId());
            }
        }

        List<FileResponse> attached = fileRepository
            .findByRefTypeAndRefId(RefType.TRAINING, saved.getTrainingId())
            .stream()
            .map(FileResponse::from)
            .toList();

        return TrainingResponse.of(saved, attached);
    }

    @Transactional
    public TrainingResponse updateTrainingWithFiles(
        Long trainingId,
        String title,
        TrainingType type,
        String description,
        String onlineUrl,
        List<Long> removeFileIds,
        List<MultipartFile> files) throws Exception {

        Training training = trainingRepository.findById(trainingId)
            .filter(t -> !t.isDeleted())
            .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        boolean hasNewAttachments = files != null && !files.isEmpty();

        if (title != null) {
            training.updateTitle(title);
        }
        if (type != null) {
            training.updateType(type);
        }
        if (description != null) {
            training.updateDescription(description);
        }
        if (onlineUrl != null) {
            training.updateOnlineUrl(onlineUrl);
        }

        if (removeFileIds != null) {
            for (Long fileId : removeFileIds) {
                FileEntity file = fileService.getFileEntity(fileId);
                file.setRefType(null);
                file.setRefId(null);
                fileRepository.save(file);
            }
        }

        if (files != null) {
            for (MultipartFile file : files) {
                FileEntity uploaded = fileService.uploadTempFile(file, FileType.TRAINING);
                fileService.bindFile(uploaded.getFileId(), RefType.TRAINING, trainingId);
            }
        }

        List<FileResponse> attached = fileRepository
            .findByRefTypeAndRefId(RefType.TRAINING, trainingId)
            .stream()
            .map(FileResponse::from)
            .toList();

        if (hasNewAttachments) {
            notifyTrainingAttachmentUpdate(training, trainingId);
        }

        return TrainingResponse.of(training, attached);
    }

    @Override
    @Transactional
    public ProgramTrainingUpdateResponse updateProgramTraining(Long programId, Long trainingId,
        ProgramTrainingUpdateRequest request) {

        OnboardingProgram program = onboardingProgramRepository.findById(programId)
            .orElseThrow(() -> new InvalidTrainingDataException(
                "프로그램을 찾을 수 없습니다. programId=" + programId
            ));

        if (program.getStatus() == ProgramStatus.COMPLETED) {
            throw new ProgramAlreadyCompletedException(programId);
        }

        ProgramTraining programTraining = programTrainingRepository
            .findByProgram_ProgramIdAndTraining_TrainingId(programId, trainingId)
            .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        ProgramTraining updated = programTraining.update(request);

        updated = programTrainingRepository.save(updated);

        return ProgramTrainingUpdateResponse.from(updated);
    }

    @Transactional
    public TrainingDeleteResponse deleteTraining(Long trainingId) {

        Training training = trainingRepository.findById(trainingId)
            .filter(t -> !t.isDeleted())
            .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        long activeCount = programTrainingRepository.countByTraining_TrainingIdAndProgram_DeletedFalse(
            trainingId);

        if (activeCount > 0) {
            throw new TrainingInUseException(trainingId, activeCount);
        }

        LocalDateTime deletedAt = LocalDateTime.now();
        training.markDeleted(deletedAt);

        List<FileEntity> files = fileRepository.findByRefTypeAndRefId(RefType.TRAINING, trainingId);

        for (FileEntity file : files) {
            file.setRefType(null);
            file.setRefId(null);
            fileRepository.save(file);
        }

        return TrainingDeleteResponse.of(trainingId, deletedAt);
    }

    @Override
    @Transactional
    public ProgramTrainingAssignResponse assignTrainingToProgram(
        Long programId,
        Long trainingId,
        ProgramTrainingAssignRequest request) {

        OnboardingProgram program = onboardingProgramRepository.findById(programId)
            .orElseThrow(
                () -> new InvalidTrainingDataException("프로그램을 찾을 수 없습니다. programId=" + programId));

        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        boolean alreadyAssigned = programTrainingRepository.existsByProgram_ProgramIdAndTraining_TrainingId(
            programId,
            trainingId);

        if (alreadyAssigned) {
            throw new InvalidTrainingDataException("이미 매핑된 교육입니다.");
        }

        LocalDateTime assignedAt = (request != null && request.getAssignedAt() != null)
            ? request.getAssignedAt()
            : LocalDateTime.now();

        ProgramTraining pt = ProgramTraining.builder()
            .program(program)
            .training(training)
            .assignedAt(assignedAt)
            .scheduledAt(request != null ? request.getScheduledAt() : null)
            .startDate(request != null ? request.getStartDate() : null)
            .endDate(request != null ? request.getEndDate() : null)
            .build();

        programTrainingRepository.save(pt);

        List<User> enrolledUsers = programEnrollmentRepository
            .findByProgram_ProgramId(programId)
            .stream()
            .filter(user -> user.getUser().getRole() == Role.USER)
            .map(ProgramEnrollment::getUser)
            .toList();

        userTrainingAssignmentService.assignForProgramTraining(pt, enrolledUsers);

        for (User mentee : enrolledUsers) {
            notificationService.notify(
                mentee,
                NotificationType.NEW_TRAINING,
                "새로운 교육이 있습니다.",
                "교육명: " + training.getTitle());
        }

        return new ProgramTrainingAssignResponse(
            programId,
            training.getTrainingId(),
            training.getTitle(),
            training.getType().name(),
            assignedAt);
    }

    @Override
    @Transactional
    public ProgramTrainingUnassignResponse unassignTrainingFromProgram(
        Long programId,
        Long trainingId) {
        OnboardingProgram program = onboardingProgramRepository.findById(programId)
            .orElseThrow(
                () -> new InvalidTrainingDataException("프로그램을 찾을 수 없습니다. programId=" + programId));

        if (program.getStatus() == ProgramStatus.COMPLETED) {
            throw new ProgramAlreadyCompletedException(programId);
        }

        ProgramTraining programTraining = programTrainingRepository
            .findByProgram_ProgramIdAndTraining_TrainingId(programId, trainingId)
            .orElseThrow(() -> new TrainingNotFoundException(trainingId));

        String title = programTraining.getTraining().getTitle();

        programTrainingRepository.delete(programTraining);

        LocalDateTime unassignedAt = LocalDateTime.now();

        return new ProgramTrainingUnassignResponse(
            programId,
            trainingId,
            title,
            unassignedAt);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramTrainingsResponse getProgramTrainings(
        Long programId,
        Boolean includeUnassigned,
        String type) {
        OnboardingProgram program = onboardingProgramRepository.findById(programId)
            .orElseThrow(
                () -> new InvalidTrainingDataException("프로그램을 찾을 수 없습니다. programId=" + programId));

        TrainingType trainingType = null;
        if (type != null) {
            try {
                trainingType = TrainingType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidTrainingDataException("지원하지 않는 교육 유형입니다. type=" + type);
            }
        }

        boolean include = Boolean.TRUE.equals(includeUnassigned);

        List<ProgramTrainingItemResponse> trainings = programTrainingQueryRepository.findProgramTrainings(
            programId,
            trainingType, include);

        return new ProgramTrainingsResponse(
            program.getProgramId(),
            program.getName(),
            program.getDescription(),
            program.getStartDate(),
            program.getEndDate(),
            trainings,
            trainings.size());
    }

    private void notifyTrainingAttachmentUpdate(Training training, Long trainingId) {
        List<ProgramTraining> programTrainings = programTrainingRepository.findByTraining_TrainingId(
            trainingId);

        Set<Long> activeProgramIds = programTrainings.stream()
            .map(ProgramTraining::getProgram)
            .filter(program -> program != null && !program.isDeleted())
            .map(program -> program.getProgramId())
            .collect(Collectors.toSet());

        if (activeProgramIds.isEmpty()) {
            return;
        }

        Set<User> mentees = activeProgramIds.stream()
            .map(programId -> programEnrollmentRepository
                .findByProgram_ProgramIdAndStatus(programId, EnrollmentStatus.ACTIVE))
            .flatMap(List::stream)
            .map(ProgramEnrollment::getUser)
            .filter(user -> user.getRole() == Role.USER && !user.isDeleted())
            .collect(Collectors.toSet());

        mentees.forEach(user -> notificationService.notify(
            user,
            NotificationType.TRAINING_ATTACHMENT_ADDED,
            "교육 자료 업데이트",
            training.getTitle() + "에 새로운 첨부 파일이 등록되었어요."));
    }
}
