package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.domain.training.dto.request.ProgramTrainingAssignRequest;
import com.j3s.yobuddy.domain.training.dto.request.TrainingCreateRequest;
import com.j3s.yobuddy.domain.training.dto.request.TrainingUpdateRequest;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingAssignResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingUnassignResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingsResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDeleteResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingListItemResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingResponse;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface TrainingAdminService {
    Page<TrainingListItemResponse> getTrainingList( TrainingType type, Long programId, String keyword, Pageable pageable);

    TrainingDetailResponse getTrainingDetail(Long trainingId);

    TrainingResponse createTraining(TrainingCreateRequest request);

    TrainingResponse updateTraining(Long trainingId, TrainingUpdateRequest request);

    TrainingDeleteResponse deleteTraining(Long trainingId);

    ProgramTrainingAssignResponse assignTrainingToProgram(Long programId, Long trainingId, ProgramTrainingAssignRequest request);

    ProgramTrainingUnassignResponse unassignTrainingFromProgram(Long programId, Long trainingId);

    ProgramTrainingsResponse getProgramTrainings(Long programId, Boolean includeUnassigned,String type);

    TrainingResponse createTrainingWithFiles(
        String title,
        TrainingType type,
        String description,
        String onlineUrl,
        List<Long> fileIds,
        List<MultipartFile> files
    ) throws Exception;

    TrainingResponse updateTrainingWithFiles(
        Long trainingId,
        String title,
        TrainingType type,
        String description,
        String onlineUrl,
        List<Long> addFileIds,
        List<Long> removeFileIds,
        List<MultipartFile> files
    ) throws Exception;
}
