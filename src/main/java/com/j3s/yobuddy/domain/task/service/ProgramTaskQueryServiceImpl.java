package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.response.ProgramTaskListResponse;
import com.j3s.yobuddy.domain.task.dto.response.ProgramTaskListResponse.TaskInfo;
import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import com.j3s.yobuddy.domain.task.repository.ProgramTaskRepository;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgramTaskQueryServiceImpl implements ProgramTaskQueryService {

    private final ProgramTaskRepository programTaskRepository;
    private final OnboardingProgramRepository programRepository;

    @Override
    public ProgramTaskListResponse getProgramTaskList(Long programId) {

        var program = programRepository.findById(programId)
            .orElseThrow(() -> new IllegalArgumentException("Program not found"));

        var programTasks = programTaskRepository.findByOnboardingProgram_ProgramId(programId);

        return ProgramTaskListResponse.builder()
            .programId(program.getProgramId())
            .programName(program.getName())
            .totalCount(programTasks.size())
            .tasks(
                programTasks.stream()
                    .map(this::toTaskInfo)
                    .collect(Collectors.toList())
            )
            .build();
    }

    private TaskInfo toTaskInfo(ProgramTask pt) {
        return TaskInfo.builder()
            .taskId(pt.getOnboardingTask().getId())
            .programId(pt.getOnboardingProgram().getProgramId())
            .dueDate(pt.getDueDate() != null ? pt.getDueDate().toLocalDate() : null)
            .assignedAt(pt.getAssignedAt())
            .build();
    }
}
