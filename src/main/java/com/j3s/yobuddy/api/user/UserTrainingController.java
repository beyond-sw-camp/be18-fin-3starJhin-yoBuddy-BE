package com.j3s.yobuddy.api.user;

import com.j3s.yobuddy.domain.training.dto.response.UserTrainingDetailResponse;
import com.j3s.yobuddy.domain.training.dto.response.UserTrainingsResponse;
import com.j3s.yobuddy.domain.training.service.UserTrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/{userId}/trainings")
@RequiredArgsConstructor
public class UserTrainingController {

    private final UserTrainingService userTrainingService;

    /**
     * 사용자(멘티) 본인의 교육 목록 조회
     *
     * GET /api/v1/users/{userId}/trainings
     *
     * Optional Query:
     * - status=PENDING/IN_PROGRESS/COMPLETED
     * - type=ONLINE/OFFLINE
     */
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public UserTrainingsResponse getUserTrainings(
        @PathVariable("userId") Long userId,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "type", required = false) String type
    ) {
        return userTrainingService.getUserTrainings(userId, status, type);
    }

    @GetMapping("/{trainingId}")
    public UserTrainingDetailResponse getUserTrainingDetail(
        @PathVariable("userId") Long userId,
        @PathVariable("trainingId") Long trainingId
    ) {
        // DTO 그대로 반환 (공통 ApiResponse 없음!)
        return userTrainingService.getUserTrainingDetail(userId, trainingId);
    }
}