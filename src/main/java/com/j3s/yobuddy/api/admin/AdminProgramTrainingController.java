package com.j3s.yobuddy.api.admin;

import com.j3s.yobuddy.domain.training.dto.request.ProgramTrainingAssignRequest;
import com.j3s.yobuddy.domain.training.dto.request.ProgramTrainingUpdateRequest;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingAssignResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingUnassignResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingUpdateResponse;
import com.j3s.yobuddy.domain.training.dto.response.ProgramTrainingsResponse;
import com.j3s.yobuddy.domain.training.service.TrainingAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/programs")
public class AdminProgramTrainingController {

    private final TrainingAdminService trainingAdminService;

    @GetMapping("/{programId}/trainings")
    @ResponseStatus(HttpStatus.OK)
    public ProgramTrainingsResponse getProgramTrainings(
        @PathVariable("programId") Long programId,
        @RequestParam(value = "includeUnassigned", required = false) Boolean includeUnassigned,
        @RequestParam(value = "type", required = false) String type
    ) {
        return trainingAdminService.getProgramTrainings(programId, includeUnassigned, type);
    }

    @PostMapping("/{programId}/trainings/{trainingId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ProgramTrainingAssignResponse assignTrainingToProgram(
        @PathVariable("programId") Long programId,
        @PathVariable("trainingId") Long trainingId,
        @RequestBody(required = false) ProgramTrainingAssignRequest request
    ) {
        return trainingAdminService.assignTrainingToProgram(programId, trainingId, request);
    }

    @PatchMapping("/{programId}/trainings/{trainingId}")
    public ResponseEntity<ProgramTrainingUpdateResponse> updateProgramTraining(
        @PathVariable("programId") Long programId,
        @PathVariable("trainingId") Long trainingId,
        @RequestBody ProgramTrainingUpdateRequest request
    ) {
        ProgramTrainingUpdateResponse result = trainingAdminService.updateProgramTraining(programId,
            trainingId, request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{programId}/trainings/{trainingId}")
    @ResponseStatus(HttpStatus.OK)
    public ProgramTrainingUnassignResponse unassignTrainingFromProgram(
        @PathVariable("programId") Long programId,
        @PathVariable("trainingId") Long trainingId
    ) {
        return trainingAdminService.unassignTrainingFromProgram(programId, trainingId);
    }
}