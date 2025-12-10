// file: com/j3s/yobuddy/api/admin/AdminTaskController.java
package com.j3s.yobuddy.api.admin;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.j3s.yobuddy.domain.task.dto.request.TaskCreateRequest;
import com.j3s.yobuddy.domain.task.dto.request.TaskUpdateRequest;
import com.j3s.yobuddy.domain.task.dto.response.AdminTaskDetailResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskCreateResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskDeleteResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskListResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskUpdateResponse;
import com.j3s.yobuddy.domain.task.service.TaskCommandService;
import com.j3s.yobuddy.domain.task.service.TaskQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/tasks")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTaskController {

    private final TaskCommandService taskCommandService;
    private final TaskQueryService taskQueryService;

    // 🔥 JSON 기반 과제 생성
    @PostMapping
    public TaskCreateResponse createTask(
        @RequestBody @Valid TaskCreateRequest request
    ) throws Exception {
        return taskCommandService.createTask(request);
    }

    // 🔥 JSON 기반 과제 수정
    @PatchMapping("/{taskId}")
    public TaskUpdateResponse updateTask(
        @PathVariable Long taskId,
        @RequestBody @Valid TaskUpdateRequest request
    ) throws Exception {
        return taskCommandService.updateTask(taskId, request);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskDetail(@PathVariable Long taskId) {
        AdminTaskDetailResponse data = taskQueryService.getTaskDetail(taskId);
        return ResponseEntity.ok(
            Map.of("statusCode", 200, "message", "Task detail retrieved successfully", "data", data)
        );
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskDeleteResponse> deleteTask(@PathVariable Long taskId) {
        TaskDeleteResponse response = taskCommandService.deleteTask(taskId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getTaskList() {
        TaskListResponse data = taskQueryService.getTaskList();
        return ResponseEntity.ok(
            Map.of("statusCode", 200, "message", "Task list retrieved successfully", "data", data)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserTasks(
        @PathVariable("userId") Long userId
    ) {
        var data = taskQueryService.getUserTaskList(userId);

        return ResponseEntity.ok(
            Map.of("statusCode", 200, "message", "User task list fetched", "data", data)
        );
    }

    
}
