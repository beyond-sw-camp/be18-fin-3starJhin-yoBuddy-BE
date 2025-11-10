package com.j3s.yobuddy.domain.programenrollment.controller;

import com.j3s.yobuddy.domain.programenrollment.dto.request.ProgramEnrollmentRequest;
import com.j3s.yobuddy.domain.programenrollment.dto.request.ProgramEnrollmentUpdateRequest;
import com.j3s.yobuddy.domain.programenrollment.dto.response.ProgramEnrollmentResponse;
import com.j3s.yobuddy.domain.programenrollment.service.ProgramEnrollmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/enrollments")
public class ProgramEnrollmentController {

    private final ProgramEnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<ProgramEnrollmentResponse> enroll(@Valid @RequestBody ProgramEnrollmentRequest request) {
        return ResponseEntity.ok(enrollmentService.enroll(request));
    }

    @GetMapping("/program/{programId}")
    public ResponseEntity<List<ProgramEnrollmentResponse>> getByProgram(@PathVariable Long programId) {
        return ResponseEntity.ok(enrollmentService.getByProgram(programId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProgramEnrollmentResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(enrollmentService.getByUser(userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProgramEnrollmentResponse> updateEnrollment(
        @PathVariable Long id,
        @Valid @RequestBody ProgramEnrollmentUpdateRequest request
    ) {
        ProgramEnrollmentResponse response = enrollmentService.updateEnrollment(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdraw(@PathVariable Long id) {
        enrollmentService.withdraw(id);
        return ResponseEntity.noContent().build();
    }
}