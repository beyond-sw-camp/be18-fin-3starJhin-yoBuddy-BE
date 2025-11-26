package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.task.dto.request.ProgramTaskAssignRequest;
import com.j3s.yobuddy.domain.task.dto.response.ProgramTaskAssignResponse;
import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import com.j3s.yobuddy.domain.task.repository.OnboardingTaskRepository;
import com.j3s.yobuddy.domain.task.repository.ProgramTaskRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramTaskCommandServiceImpl implements ProgramTaskCommandService {

    private final OnboardingProgramRepository programRepository;
    private final OnboardingTaskRepository taskRepository;
    private final ProgramTaskRepository programTaskRepository;

    @Override
    public ProgramTaskAssignResponse assignTask(Long programId, Long taskId, ProgramTaskAssignRequest request) {

        var program = programRepository.findById(programId)
            .orElseThrow(() -> new IllegalArgumentException("Program not found"));

        var task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // 중복 할당 방지 (정확한 메서드 이름)
        if (programTaskRepository.existsByOnboardingProgram_ProgramIdAndOnboardingTask_Id(programId, taskId)) {
            throw new IllegalStateException("Task already assigned to this program");
        }

        // LocalDate → LocalDateTime 변환 (23:59:59 고정)
        LocalDateTime dueDateTime = request.getDueDate().atTime(23, 59, 59);

        // ProgramTask 생성
        ProgramTask programTask = ProgramTask.builder()
            .onboardingProgram(program)
            .onboardingTask(task)
            .dueDate(dueDateTime)     // ✅ 수정됨 (LocalDateTime)
            .build();                 // assignedAt은 엔티티에서 자동 생성

        ProgramTask saved = programTaskRepository.save(programTask);

        return ProgramTaskAssignResponse.of(saved);
    }

    @Override
    public void unassignTask(Long programId, Long taskId) {

        var programTask = programTaskRepository
            .findByOnboardingProgram_ProgramIdAndOnboardingTask_Id(programId, taskId)
            .orElseThrow(() -> new IllegalArgumentException("ProgramTask not found"));

        programTaskRepository.delete(programTask);
    }
}

