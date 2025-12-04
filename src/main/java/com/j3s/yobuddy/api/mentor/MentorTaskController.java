package com.j3s.yobuddy.api.mentor;

import com.j3s.yobuddy.domain.task.dto.request.TaskGradeRequest;
import com.j3s.yobuddy.domain.task.service.MentorTaskService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentors")
@PreAuthorize("hasRole('MENTOR')")
public class MentorTaskController {

    private final MentorTaskService mentorTaskService;

    /**
     * 내 모든 멘티들의 과제 리스트 조회
     */
    @GetMapping("/{mentorId}/tasks")
    public ResponseEntity<?> getAllMenteeTasks(
        @PathVariable Long mentorId,
        Authentication authentication
    ) {
        Long authUserId = Long.valueOf(authentication.getName());
        if (!authUserId.equals(mentorId)) {
            return ResponseEntity.status(403).body("FORBIDDEN");
        }

        var data = mentorTaskService.getAllMenteeTasks(mentorId);

        return ResponseEntity.ok(Map.of(
            "statusCode", 200,
            "message", "Mentee tasks fetched successfully",
            "data", data
        ));
    }

    /**
     * 특정 멘티 과제 상세 정보 조회
     */
    @GetMapping("/{mentorId}/tasks/{userTaskId}")
    public ResponseEntity<?> getTaskDetail(
        @PathVariable Long mentorId,
        @PathVariable Long userTaskId,
        Authentication authentication
    ) {
        Long authUserId = Long.valueOf(authentication.getName());
        if (!authUserId.equals(mentorId)) {
            return ResponseEntity.status(403).body("FORBIDDEN");
        }

        var data = mentorTaskService.getTaskDetail(mentorId, userTaskId);

        return ResponseEntity.ok(Map.of(
            "statusCode", 200,
            "message", "Task detail fetched",
            "data", data
        ));
    }

    /**
     * 과제 채점(점수 + 피드백)
     */
    @PatchMapping("/{mentorId}/tasks/{userTaskId}/grade")
    public ResponseEntity<?> gradeTask(
        @PathVariable Long mentorId,
        @PathVariable Long userTaskId,
        @RequestBody TaskGradeRequest request,
        Authentication authentication
    ) {
        Long authUserId = Long.valueOf(authentication.getName());
        if (!authUserId.equals(mentorId)) {
            return ResponseEntity.status(403).body("FORBIDDEN");
        }

        mentorTaskService.gradeTask(mentorId, userTaskId, request);

        return ResponseEntity.ok(Map.of(
            "statusCode", 200,
            "message", "Task graded successfully"
        ));
    }
}
