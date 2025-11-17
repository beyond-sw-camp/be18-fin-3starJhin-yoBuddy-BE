package com.j3s.yobuddy.api.admin;

import com.j3s.yobuddy.domain.training.dto.request.TrainingCreateRequest;
import com.j3s.yobuddy.domain.training.dto.request.TrainingUpdateRequest;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDeleteResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingListItemResponse;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.service.TrainingAdminService;
import com.j3s.yobuddy.domain.training.dto.response.TrainingResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/trainings")
@RequiredArgsConstructor
public class AdminTrainingController {

    private final TrainingAdminService trainingAdminService;

    @GetMapping
    public Page<TrainingListItemResponse> getTrainings(
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "programId", required = false) Long programId,
        @RequestParam(value = "keyword", required = false) String keyword,
        @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        TrainingType trainingType = null;
        if (type != null && !type.isBlank()) {
            trainingType = TrainingType.valueOf(type.toUpperCase());
        }

        return trainingAdminService.getTrainingList(trainingType, programId, keyword, pageable);
    }

    @GetMapping("/{training-id}")
    public TrainingDetailResponse getTrainingDetail(
        @PathVariable("training-id") Long trainingId
    ) {
        return trainingAdminService.getTrainingDetail(trainingId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainingResponse createTraining(
        @Valid @RequestBody TrainingCreateRequest request
    ) {
        return trainingAdminService.createTraining(request);
    }

    @PatchMapping("/{trainingId}")
    @ResponseStatus(HttpStatus.OK)
    public TrainingResponse updateTraining(
        @PathVariable("trainingId") Long trainingId,
        @RequestBody TrainingUpdateRequest request
    ) {
        return trainingAdminService.updateTraining(trainingId, request);
    }

    @DeleteMapping("/{trainingId}")
    public ResponseEntity<TrainingDeleteResponse> deleteTraining(
        @PathVariable("trainingId") Long trainingId
    ) {
        TrainingDeleteResponse response = trainingAdminService.deleteTraining(trainingId);
        return ResponseEntity.ok(response);
    }
}