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
@RequestMapping("/api/v1/admin/programs/{programId}/enrollments")
public class AdminProgramEnrollmentController {

    private final ProgramEnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<List<ProgramEnrollmentResponse>> enroll(
        @PathVariable Long programId,
        @Valid @RequestBody ProgramEnrollmentRequest request
    ) {
        return ResponseEntity.ok(enrollmentService.enroll(programId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProgramEnrollmentResponse>> getByProgram(
        @PathVariable Long programId
    ) {
        return ResponseEntity.ok(enrollmentService.getByProgram(programId));
    }

    @PatchMapping("/{enrollmentId}")
    public ResponseEntity<ProgramEnrollmentResponse> updateEnrollment(
        @PathVariable Long programId,
        @PathVariable Long enrollmentId,
        @Valid @RequestBody ProgramEnrollmentUpdateRequest request
    ) {
        return ResponseEntity.ok(enrollmentService.updateEnrollment(programId, enrollmentId, request));
    }

    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> withdraw(
        @PathVariable Long programId,
        @PathVariable Long enrollmentId
    ) {
        enrollmentService.withdraw(programId, enrollmentId);
        return ResponseEntity.noContent().build();
    }
}
