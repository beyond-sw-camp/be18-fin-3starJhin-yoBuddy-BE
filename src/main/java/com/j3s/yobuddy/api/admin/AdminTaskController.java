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

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/tasks")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTaskController {

    private final TaskCommandService taskCommandService;
    private final TaskQueryService taskQueryService;

    @PostMapping
    public ResponseEntity<TaskCreateResponse> createTask(
        @Valid @RequestBody TaskCreateRequest request
    ) {
        TaskCreateResponse response = taskCommandService.createTask(request);

        return ResponseEntity
            .created(URI.create("/api/v1/admin/tasks/" + response.getTaskId()))
            .body(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskDetail(@PathVariable Long taskId) {

        AdminTaskDetailResponse data = taskQueryService.getTaskDetail(taskId);

        return ResponseEntity.ok(
            Map.of(
                "statusCode", 200,
                "message", "Task detail retrieved successfully",
                "data", data
            )
        );
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskUpdateResponse> updateTask(
        @PathVariable Long taskId,
        @Valid @RequestBody TaskUpdateRequest request
    ) {
        TaskUpdateResponse response = taskCommandService.updateTask(taskId, request);

        return ResponseEntity.ok(response);
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
            Map.of(
                "statusCode", 200,
                "message", "Task list retrieved successfully",
                "data", data
            )
        );
    }
}
