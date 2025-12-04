package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment;
import com.j3s.yobuddy.domain.programenrollment.repository.ProgramEnrollmentRepository;
import com.j3s.yobuddy.domain.task.dto.request.ProgramTaskAssignRequest;
import com.j3s.yobuddy.domain.task.dto.response.ProgramTaskAssignResponse;
import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import com.j3s.yobuddy.domain.task.repository.OnboardingTaskRepository;
import com.j3s.yobuddy.domain.task.repository.ProgramTaskRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
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

    private final ProgramEnrollmentRepository enrollmentRepository;
    private final UserTaskAssignmentService userTaskAssignmentService;

    @Override
    public ProgramTaskAssignResponse assignTask(Long programId, Long taskId, ProgramTaskAssignRequest request) {

        var program = programRepository.findById(programId)
            .orElseThrow(() -> new IllegalArgumentException("Program not found"));

        var task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (programTaskRepository.existsByOnboardingProgram_ProgramIdAndOnboardingTask_Id(programId, taskId)) {
            throw new IllegalStateException("Task already assigned to this program");
        }

        LocalDateTime dueDateTime = request.getDueDate().atTime(23, 59, 59);

        ProgramTask programTask = ProgramTask.builder()
            .onboardingProgram(program)
            .onboardingTask(task)
            .dueDate(dueDateTime)
            .build();

        ProgramTask saved = programTaskRepository.save(programTask);

        List<User> enrolledUsers = enrollmentRepository.findByProgram_ProgramId(programId)
            .stream()
            .map(ProgramEnrollment::getUser)
            .toList();

        userTaskAssignmentService.assignForProgramTask(saved, enrolledUsers);

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
