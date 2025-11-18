package com.j3s.yobuddy.api.user;

import com.j3s.yobuddy.domain.mentoring.dto.response.MentoringSessionResponse;
import com.j3s.yobuddy.domain.mentoring.service.MentoringSessionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/{userId}/mentoring")
@RequiredArgsConstructor
public class UserMentoringController {

    private final MentoringSessionService service;

    @GetMapping("/sessions")
    public ResponseEntity<List<MentoringSessionResponse>> getSessions(
        @PathVariable Long userId
    ) {
        return ResponseEntity.ok(service.getByMentee(userId));
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<MentoringSessionResponse> getOne(
        @PathVariable Long userId,
        @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(service.get(sessionId));
    }

    @GetMapping("/feedbacks")
    public ResponseEntity<List<String>> getFeedbacks(
        @PathVariable Long userId
    ) {
        List<String> feedbacks =
            service.getByMentee(userId)
                .stream()
                .map(MentoringSessionResponse::getFeedback)
                .filter(f -> f != null && !f.isBlank())
                .toList();

        return ResponseEntity.ok(feedbacks);
    }
}
