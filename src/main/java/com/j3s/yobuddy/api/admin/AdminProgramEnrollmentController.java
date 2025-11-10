package com.j3s.yobuddy.api.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.j3s.yobuddy.domain.programenrollment.dto.request.ProgramEnrollmentRequest;
import com.j3s.yobuddy.domain.programenrollment.dto.request.ProgramEnrollmentUpdateRequest;
import com.j3s.yobuddy.domain.programenrollment.dto.response.ProgramEnrollmentResponse;
import com.j3s.yobuddy.domain.programenrollment.service.ProgramEnrollmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/enrollments")
public class AdminProgramEnrollmentController {

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