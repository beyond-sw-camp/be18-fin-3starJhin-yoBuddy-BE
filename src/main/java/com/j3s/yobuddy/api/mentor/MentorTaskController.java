package com.j3s.yobuddy.api.mentor;

import com.j3s.yobuddy.domain.task.dto.request.TaskGradeRequest;
import com.j3s.yobuddy.domain.task.dto.response.MentorMenteeTaskResponse;
import com.j3s.yobuddy.domain.task.service.MentorTaskService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentors")
@PreAuthorize("hasRole('BUDDY')")
public class MentorTaskController {

    private final MentorTaskService mentorTaskService;

    /**
     * 멘토가 멘티의 제출 과제를 채점한다.
     */
    @PatchMapping("/{mentorId}/tasks/{programTaskId}/grade")
    public ResponseEntity<?> gradeTask(
        @PathVariable Long mentorId,
        @PathVariable Long programTaskId,
        @RequestParam Long menteeId,
        @RequestBody TaskGradeRequest request,
        Authentication authentication
    ) {

        Long authUserId = Long.valueOf(authentication.getName());
        if (!authUserId.equals(mentorId)) {
            return ResponseEntity.status(403).body(
                Map.of(
                    "status", 403,
                    "message", "FORBIDDEN_OPERATION",
                    "detail", "You can only grade tasks assigned to you."
                )
            );
        }

        mentorTaskService.gradeTask(mentorId, menteeId, programTaskId, request);

        return ResponseEntity.ok(
            Map.of(
                "statusCode", 200,
                "message", "Task graded successfully"
            )
        );
    }

    /**
     * 멘토가 특정 멘티의 제출한 과제 목록 조회
     */
    @GetMapping("/{mentorId}/mentees/{menteeId}/tasks")
    public ResponseEntity<?> getSubmittedTasks(
        @PathVariable Long mentorId,
        @PathVariable Long menteeId,
        Authentication authentication
    ) {

        Long authUserId = Long.valueOf(authentication.getName());
        if (!authUserId.equals(mentorId)) {
            return ResponseEntity.status(403).body(
                Map.of(
                    "status", 403,
                    "message", "FORBIDDEN_OPERATION",
                    "detail", "You can only access your assigned mentees."
                )
            );
        }

        MentorMenteeTaskResponse data =
            mentorTaskService.getSubmittedTasks(mentorId, menteeId);

        return ResponseEntity.ok(
            Map.of(
                "statusCode", 200,
                "message", "Submitted tasks retrieved successfully",
                "data", data
            )
        );
    }
}
