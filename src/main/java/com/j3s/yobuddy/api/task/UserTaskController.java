package com.j3s.yobuddy.api.task;

import com.j3s.yobuddy.domain.task.dto.request.UserTaskSubmitRequest;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskListResponse;
import com.j3s.yobuddy.domain.task.entity.UserTaskStatus;
import com.j3s.yobuddy.domain.task.service.UserTaskCommandService;
import com.j3s.yobuddy.domain.task.service.UserTaskQueryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('USER')")
public class UserTaskController {

    private final UserTaskQueryService userTaskQueryService;
    private final UserTaskCommandService userTaskCommandService;

    @GetMapping("/{userId}/tasks")
    public ResponseEntity<?> getUserTasks(
        @PathVariable Long userId,
        @RequestParam(required = false) UserTaskStatus status,
        @RequestParam(required = false) Long programId,
        @RequestParam(required = false) Boolean overdue,
        Authentication authentication
    ) {

        // 1) JWT에서 현재 사용자 ID 얻기
        Long authUserId = Long.valueOf(authentication.getName());

        // 2) 본인 확인
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(403).body(
                Map.of(
                    "status", 403,
                    "message", "FORBIDDEN_OPERATION",
                    "detail", "User " + userId + " can only access their own tasks."
                )
            );
        }

        // 3) 서비스 호출
        UserTaskListResponse data = userTaskQueryService.getUserTasks(
            userId,
            status,
            programId,
            overdue
        );

        // 4) 정상 응답
        return ResponseEntity.ok(
            Map.of(
                "statusCode", 200,
                "message", "User task list fetched successfully",
                "data", data
            )
        );
    }

    @PostMapping("/{userId}/tasks/{programTaskId}/submit")
    public ResponseEntity<?> submitTask(
        @PathVariable Long userId,
        @PathVariable Long programTaskId,
        @RequestBody UserTaskSubmitRequest request,
        Authentication authentication
    ) {

        // JWT 의 userId 가져오기
        Long authUserId = Long.valueOf(authentication.getName());

        // 본인 확인 (다른 사람의 제출 방지)
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(403).body(
                Map.of(
                    "status", 403,
                    "message", "FORBIDDEN_OPERATION",
                    "detail", "You can only submit your own tasks."
                )
            );
        }

        userTaskCommandService.submitTask(userId, programTaskId, request);

        return ResponseEntity.status(201).body(
            Map.of(
                "statusCode", 201,
                "message", "Task submitted successfully"
            )
        );
    }

    @GetMapping("/{userId}/tasks/{programTaskId}/score")
    public ResponseEntity<?> getUserTaskScore(
        @PathVariable Long userId,
        @PathVariable Long programTaskId,
        Authentication authentication
    ) {
        // 1) JWT의 userId 가져오기
        Long authUserId = Long.valueOf(authentication.getName());

        // 2) 본인 확인
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(403).body(
                Map.of(
                    "status", 403,
                    "message", "FORBIDDEN_OPERATION",
                    "detail", "User " + userId + " can only access their own task scores."
                )
            );
        }

        // 3) 서비스 호출
        var data = userTaskQueryService.getUserTaskScore(userId, programTaskId);

        // 4) 성공 응답
        return ResponseEntity.ok(
            Map.of(
                "statusCode", 200,
                "message", "User task score fetched successfully",
                "data", data
            )
        );
    }

}
