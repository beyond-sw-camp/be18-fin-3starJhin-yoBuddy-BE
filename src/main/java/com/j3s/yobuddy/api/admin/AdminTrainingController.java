package com.j3s.yobuddy.api.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.j3s.yobuddy.domain.file.service.FileService;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDeleteResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingListItemResponse;
import com.j3s.yobuddy.domain.training.dto.response.TrainingResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingsResponse;
import com.j3s.yobuddy.domain.training.entity.TrainingType;
import com.j3s.yobuddy.domain.training.service.TrainingAdminService;
import com.j3s.yobuddy.domain.training.service.UserTrainingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/trainings")
@RequiredArgsConstructor
public class AdminTrainingController {

    private final TrainingAdminService trainingAdminService;
    private final FileService fileService;
    private final UserTrainingService userTrainingService;

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

    @GetMapping("/{trainingId}")
    public ResponseEntity<TrainingDetailResponse> getDetail(@PathVariable Long trainingId) {
        return ResponseEntity.ok(trainingAdminService.getTrainingDetail(trainingId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TrainingResponse createTraining(
        @RequestParam String title,
        @RequestParam TrainingType type,
        @RequestParam String description,
        @RequestParam(required = false) String onlineUrl,
        @RequestPart(required = false) List<MultipartFile> files
    ) throws Exception {
        return trainingAdminService.createTrainingWithFiles(
            title, type, description, onlineUrl, files
        );
    }

    @PatchMapping(value = "/{trainingId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public TrainingResponse updateTraining(
        @PathVariable Long trainingId,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) TrainingType type,
        @RequestParam(required = false) String description,
        @RequestParam(required = false) String onlineUrl,
        @RequestParam(required = false) List<Long> removeFileIds,
        @RequestPart(required = false) List<MultipartFile> files
    ) throws Exception {
        return trainingAdminService.updateTrainingWithFiles(
            trainingId, title, type, description, onlineUrl, removeFileIds, files
        );
    }

    @DeleteMapping("/{trainingId}")
    public ResponseEntity<TrainingDeleteResponse> deleteTraining(@PathVariable Long trainingId) {
        return ResponseEntity.ok(trainingAdminService.deleteTraining(trainingId));
    }

    @GetMapping("/user/{userId}")
    public UserTrainingsResponse getUserTrainings(
        @PathVariable("userId") Long userId,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "type", required = false) String type
    ) {
        return userTrainingService.getUserTrainings(userId, status, type);
    }
    
}