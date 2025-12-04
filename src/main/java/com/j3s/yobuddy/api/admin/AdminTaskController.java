// file: com/j3s/yobuddy/api/admin/AdminTaskController.java
package com.j3s.yobuddy.api.admin;

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
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}
