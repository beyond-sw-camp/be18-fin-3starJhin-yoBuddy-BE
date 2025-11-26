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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/tasks")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTaskController {

    private final TaskCommandService taskCommandService;
    private final TaskQueryService taskQueryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TaskCreateResponse createTask(
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("points") Integer points,
        @RequestParam("departmentIds") String departmentIdsRaw,
        @RequestParam(value = "files", required = false) List<MultipartFile> files
    )throws Exception {

        List<Long> departmentIds = Arrays.stream(departmentIdsRaw.split(","))
            .map(Long::parseLong)
            .toList();

        return taskCommandService.createTaskWithFiles(
            title, description, points, departmentIds, files
        );
    }

    @PatchMapping(value = "/{taskId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TaskUpdateResponse updateTask(
        @PathVariable Long taskId,
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "description", required = false) String description,
        @RequestParam(value = "points", required = false) Integer points,
        @RequestParam(value = "departmentIds", required = false) List<Long> departmentIds,
        @RequestParam(value = "removeFileIds", required = false) List<Long> removeFileIds,
        @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        return taskCommandService.updateTaskWithFiles(
            taskId, title, description, points,
            departmentIds, removeFileIds, files
        );
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
