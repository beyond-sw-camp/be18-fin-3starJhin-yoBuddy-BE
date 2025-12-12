package com.j3s.yobuddy.api.admin;

import com.j3s.yobuddy.domain.task.dto.request.ProgramTaskAssignRequest;
import com.j3s.yobuddy.domain.task.dto.request.ProgramTaskUpdateRequest;
import com.j3s.yobuddy.domain.task.dto.response.ProgramTaskListResponse;
import com.j3s.yobuddy.domain.task.dto.response.ProgramTaskUpdateResponse;
import com.j3s.yobuddy.domain.task.service.ProgramTaskCommandService;
import com.j3s.yobuddy.domain.task.service.ProgramTaskQueryService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/programs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProgramTaskController {

    private final ProgramTaskCommandService programTaskCommandService;
    private final ProgramTaskQueryService programTaskQueryService;

    // 과제 할당
    @PostMapping("/{programId}/tasks/{taskId}")
    public ResponseEntity<?> assignTask(
        @PathVariable Long programId,
        @PathVariable Long taskId,
        @Valid @RequestBody ProgramTaskAssignRequest request
    ) {

        var data = programTaskCommandService.assignTask(programId, taskId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of(
                "statusCode", 201,
                "message", "Task assigned to program successfully",
                "data", data
            )
        );
    }

    @PatchMapping("/{programId}/tasks/{taskId}")
    public ResponseEntity<ProgramTaskUpdateResponse> updateTask(
        @PathVariable Long programId,
        @PathVariable Long taskId,
        @RequestBody ProgramTaskUpdateRequest request
    ) {
        ProgramTaskUpdateResponse result = programTaskCommandService.updateTask(programId, taskId,
            request);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{programId}/tasks/{taskId}")
    public ResponseEntity<?> unassignTask(
        @PathVariable Long programId,
        @PathVariable Long taskId
    ) {

        programTaskCommandService.unassignTask(programId, taskId);

        return ResponseEntity.ok(
            Map.of(
                "statusCode", 200,
                "message", "Task unassigned from program successfully"
            )
        );
    }


    /**
     * 프로그램에 속한 Task 목록 조회
     */
    @GetMapping("/{programId}/tasks")
    public ResponseEntity<?> getProgramTaskList(@PathVariable Long programId) {

        ProgramTaskListResponse data = programTaskQueryService.getProgramTaskList(programId);

        return ResponseEntity.ok(
            Map.of(
                "statusCode", 200,
                "message", "Program task list retrieved successfully",
                "data", data
            )
        );
    }
}
