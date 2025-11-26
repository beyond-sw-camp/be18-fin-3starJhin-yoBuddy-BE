package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.file.service.FileService;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingItemResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingsResponse;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.entity.UserTraining;
import com.j3s.yobuddy.domain.training.entity.UserTrainingStatus;
import com.j3s.yobuddy.domain.training.exception.InvalidTrainingDataException;
import com.j3s.yobuddy.domain.training.exception.UserTrainingsNotFoundException;
import com.j3s.yobuddy.domain.training.repository.UserTrainingQueryRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserTrainingServiceImpl implements UserTrainingService {

    private final UserTrainingQueryRepository userTrainingQueryRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    @Override
    @Transactional(readOnly = true)
    public UserTrainingsResponse getUserTrainings(Long userId, String status, String type) {

        UserTrainingStatus userTrainingStatus = null;
        if (status != null) {
            try {
                userTrainingStatus = UserTrainingStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidTrainingDataException("지원하지 않는 교육 진행 상태입니다. status=" + status);
            }
        }

        TrainingType trainingType = null;
        if (type != null) {
            try {
                trainingType = TrainingType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidTrainingDataException("지원하지 않는 교육 유형입니다. type=" + type);
            }
        }

        List<UserTrainingItemResponse> trainings =
            userTrainingQueryRepository.findUserTrainings(userId, userTrainingStatus, trainingType);

        if (trainings.isEmpty()) {
            throw new UserTrainingsNotFoundException(userId);
        }

        return new UserTrainingsResponse(userId, trainings, trainings.size());
    }

    @Override
    @Transactional(readOnly = true)
    public UserTrainingDetailResponse getUserTrainingDetail(Long pathUserId, Long trainingId) {

        UserTrainingDetailResponse dto = userTrainingQueryRepository
            .findUserTrainingDetail(pathUserId, trainingId)
            .orElseThrow(() -> new UserTrainingsNotFoundException(pathUserId));

        // 사용자 제출 파일(UserTraining 기준)
        List<FileResponse> files =
            fileRepository.findByRefTypeAndRefId(RefType.USER_TRAINING, dto.getUserTrainingId()).stream()
                .map(FileResponse::from)
                .toList();

        dto.setAttachedFiles(files);
        return dto;
    }

    @Override
    @Transactional
    public void uploadCertificate(Long userId, Long trainingId, List<MultipartFile> files) {

        UserTraining ut = userTrainingQueryRepository
            .findEntity(userId, trainingId)
            .orElseThrow(() -> new UserTrainingsNotFoundException(userId));

        if (ut.getProgramTraining().getTraining().getType() != TrainingType.ONLINE) {
            throw new IllegalStateException("온라인 교육만 이수증을 제출할 수 있습니다.");
        }

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("이수증 파일을 최소 1개 이상 업로드해야 합니다.");
        }

        for (MultipartFile file : files) {
            try {
                FileEntity uploaded = fileService.uploadTempFile(file, FileType.USER_TRAINING);
                fileService.bindFile(uploaded.getFileId(), RefType.USER_TRAINING, ut.getUserTrainingId());
            } catch (Exception e) {
                throw new RuntimeException("파일 업로드 중 오류 발생", e);
            }
        }

        ut.completeTraining();
    }
}

