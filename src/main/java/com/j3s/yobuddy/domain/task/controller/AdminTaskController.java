package com.j3s.yobuddy.domain.task.controller;

import com.j3s.yobuddy.domain.task.dto.request.AdminTaskSearchCond;
import com.j3s.yobuddy.domain.task.dto.request.TaskCreateRequest;
import com.j3s.yobuddy.domain.task.dto.request.TaskUpdateRequest;
import com.j3s.yobuddy.domain.task.dto.response.AdminTaskListResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskCreateResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskDeleteResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskUpdateResponse;
import com.j3s.yobuddy.domain.task.service.TaskQueryService;
import com.j3s.yobuddy.domain.task.service.TaskCommandService;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/tasks")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTaskController {

    private final TaskQueryService taskQueryService;
    private final TaskCommandService taskCommandService; // ⭐ 추가해야 하는 부분

    @GetMapping
    public ResponseEntity<AdminTaskListResponse> getTaskList(
        @RequestParam(required = false) Long programId,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String status,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueBefore,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueAfter,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createdAt") String sort
    ) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort).descending());

        AdminTaskSearchCond cond = new AdminTaskSearchCond();
        cond.setProgramId(programId);
        cond.setKeyword(keyword);
        cond.setStatus(status);
        cond.setDueBefore(dueBefore);
        cond.setDueAfter(dueAfter);

        AdminTaskListResponse response = taskQueryService.getAdminTaskList(cond, pageable);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TaskCreateResponse> createTask(
        @Valid @RequestBody TaskCreateRequest request
    ) {
        TaskCreateResponse response = taskCommandService.createTask(request);

        return ResponseEntity
            .created(URI.create("/api/v1/admin/tasks/" + response.getTaskId()))
            .body(response);
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskUpdateResponse> updateTask(
        @PathVariable Long taskId,
        @RequestBody TaskUpdateRequest request
    ) {
        TaskUpdateResponse response = taskCommandService.updateTask(taskId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskDeleteResponse> deleteTask(
        @PathVariable Long taskId
    ) {
        TaskDeleteResponse response = taskCommandService.deleteTask(taskId);
        return ResponseEntity.ok(response);
    }


}
