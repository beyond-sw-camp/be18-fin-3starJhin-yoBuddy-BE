package com.j3s.yobuddy.api.user;

import com.j3s.yobuddy.domain.task.dto.request.TaskSubmitRequest;
import com.j3s.yobuddy.domain.task.service.UserTaskCommandService;
import com.j3s.yobuddy.domain.task.service.UserTaskQueryService;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('USER')")
public class UserTaskController {

    private final UserTaskQueryService userTaskQueryService;
    private final UserTaskCommandService userTaskCommandService;

    /** 유저 과제 목록 */
    @GetMapping("/{userId}/tasks")
    public ResponseEntity<?> getUserTasks(
        @PathVariable Long userId,
        Authentication authentication
    ) {
        Long authUserId = Long.valueOf(authentication.getName());
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(403).body("FORBIDDEN");
        }

        var data = userTaskQueryService.getUserTaskList(userId);

        return ResponseEntity.ok(
            Map.of("statusCode", 200, "message", "User task list fetched", "data", data)
        );
    }

    /** UserTask 상세 조회 */
    @GetMapping("/{userId}/tasks/{userTaskId}")
    public ResponseEntity<?> getUserTaskDetail(
        @PathVariable Long userId,
        @PathVariable Long userTaskId,
        Authentication authentication
    ) {
        Long authUserId = Long.valueOf(authentication.getName());
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(403).body("FORBIDDEN");
        }

        var data = userTaskQueryService.getUserTaskDetail(userId, userTaskId);

        return ResponseEntity.ok(
            Map.of("statusCode", 200, "message", "User task detail fetched", "data", data)
        );
    }

    /** UserTask 제출 */
    @PostMapping(
        value = "/{userId}/tasks/{userTaskId}/submit",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> submitTask(
        @PathVariable Long userId,
        @PathVariable Long userTaskId,
        @RequestPart(value = "files", required = false) MultipartFile[] files, // 🔥 먼저
        @RequestParam(value = "comment", required = false) String comment,     // 🔥 나중에
        Authentication authentication
    ) throws Exception {

        System.out.println("COMMENT TEST = " + comment); // 🔥 확인용 로그

        Long authUserId = Long.valueOf(authentication.getName());
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(403).body("FORBIDDEN");
        }

        TaskSubmitRequest request = new TaskSubmitRequest(comment, files);

        userTaskCommandService.submitTaskWithFiles(
            userId,
            userTaskId,
            request
        );

        return ResponseEntity.status(201)
            .body(Map.of("statusCode", 201, "message", "Task submitted successfully"));
    }


    /** 점수 조회 */
    @GetMapping("/{userId}/tasks/{userTaskId}/score")
    public ResponseEntity<?> getUserTaskScore(
        @PathVariable Long userId,
        @PathVariable Long userTaskId,
        Authentication authentication
    ) {
        Long authUserId = Long.valueOf(authentication.getName());
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(403).body("FORBIDDEN");
        }

        var data = userTaskQueryService.getUserTaskScore(userId, userTaskId);

        return ResponseEntity.ok(
            Map.of("statusCode", 200, "message", "User task score fetched", "data", data)
        );
    }
}
