package com.j3s.yobuddy.api.admin;

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
@RequestMapping("/api/v1/admin/mentoring/sessions")
@RequiredArgsConstructor
public class AdminMentoringSessionController {
    private final MentoringSessionService service;

    @GetMapping
    public ResponseEntity<List<MentoringSessionResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<MentoringSessionResponse> getOne(
        @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(service.get(sessionId));
    }
}
