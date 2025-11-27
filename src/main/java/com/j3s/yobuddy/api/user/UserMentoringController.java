package com.j3s.yobuddy.api.user;

import com.j3s.yobuddy.domain.mentoring.dto.response.MentoringSessionResponse;
import com.j3s.yobuddy.domain.mentoring.entity.MentoringStatus;
import com.j3s.yobuddy.domain.mentoring.service.MentoringSessionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/{userId}/mentoring")
@RequiredArgsConstructor
public class UserMentoringController {

    private final MentoringSessionService service;

    @GetMapping("/sessions")
    public ResponseEntity<Page<MentoringSessionResponse>> getSessions(
        @PathVariable Long userId,
        @RequestParam(required = false) MentoringStatus status,
        @RequestParam(required = false) String query,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            service.searchSessions(
                null,
                userId,
                null,
                status,
                query,
                pageable
            )
        );
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<MentoringSessionResponse> getOne(
        @PathVariable Long userId,
        @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(service.get(sessionId));
    }

    @GetMapping("/feedbacks")
    public ResponseEntity<Page<String>> getFeedbacks(
        @PathVariable Long userId,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        Page<MentoringSessionResponse> sessions = service.getByMentee(userId, pageable);

        Page<String> feedbacks =
            sessions.map(MentoringSessionResponse::getFeedback)
                .map(f -> (f == null || f.isBlank()) ? null : f);

        return ResponseEntity.ok(feedbacks);
    }
}

