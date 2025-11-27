package com.j3s.yobuddy.api.admin;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/mentoring/sessions")
@RequiredArgsConstructor
public class AdminMentoringSessionController {

    private final MentoringSessionService service;

    @GetMapping
    public ResponseEntity<Page<MentoringSessionResponse>> getAll(
        @RequestParam(required = false) MentoringStatus status,
        @RequestParam(required = false) String query,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            service.searchSessions(
                null,
                null,
                null,
                status,
                query,
                pageable
            )
        );
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<MentoringSessionResponse> getOne(
        @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(service.get(sessionId));
    }
}
