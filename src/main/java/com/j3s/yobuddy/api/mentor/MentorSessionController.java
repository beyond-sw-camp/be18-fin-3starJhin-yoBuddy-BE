package com.j3s.yobuddy.api.mentor;

import com.j3s.yobuddy.domain.mentor.mentoring.dto.request.MentoringSessionCreateRequest;
import com.j3s.yobuddy.domain.mentor.mentoring.dto.request.MentoringSessionUpdateRequest;
import com.j3s.yobuddy.domain.mentor.mentoring.dto.response.MentoringSessionResponse;
import com.j3s.yobuddy.domain.mentor.mentoring.entity.MentoringStatus;
import com.j3s.yobuddy.domain.mentor.mentoring.service.MentoringSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mentors/{mentorId}/sessions")
@RequiredArgsConstructor
public class MentorSessionController {

    private final MentoringSessionService service;

    @GetMapping
    public ResponseEntity<Page<MentoringSessionResponse>> getSessions(
        @PathVariable Long mentorId,
        @RequestParam(required = false) MentoringStatus status,
        @RequestParam(required = false) String query,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            service.searchSessions(
                mentorId,
                null,
                null,
                status,
                query,
                pageable
            )
        );
    }

    @PostMapping
    public ResponseEntity<MentoringSessionResponse> create(
        @PathVariable Long mentorId,
        @RequestBody MentoringSessionCreateRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<MentoringSessionResponse> getOne(
        @PathVariable Long mentorId,
        @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(service.get(sessionId));
    }

    @PatchMapping("/{sessionId}/feedback")
    public ResponseEntity<MentoringSessionResponse> updateFeedback(
        @PathVariable Long mentorId,
        @PathVariable Long sessionId,
        @RequestBody MentoringSessionUpdateRequest req
    ) {
        return ResponseEntity.ok(service.update(sessionId, req));
    }
}


