package com.j3s.yobuddy.domain.training.service;

import com.j3s.yobuddy.domain.training.dto.response.UserTrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingsResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface UserTrainingService {
    UserTrainingsResponse getUserTrainings(Long userId, String status, String type);
    UserTrainingDetailResponse getUserTrainingDetail(Long pathUserId, Long trainingId);
    void uploadCertificate(Long userId, Long trainingId, List<MultipartFile> files);
}
