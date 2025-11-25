package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.response.UserTaskListResponse;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskListResponse.TaskInfo;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskScoreResponse;
import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.entity.UserTaskStatus;
import com.j3s.yobuddy.domain.task.repository.ProgramTaskRepository;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserTaskQueryServiceImpl implements UserTaskQueryService {

    private final ProgramTaskRepository programTaskRepository;
    private final UserTaskRepository userTaskRepository;

    @Override
    public UserTaskListResponse getUserTasks(
        Long userId,
        UserTaskStatus statusFilter,
        Long programId,
        Boolean overdue
    ) {

        // 1) 특정 프로그램의 과제만 조회하거나, 전체 프로그램 과제 조회
        List<ProgramTask> programTasks = (programId != null)
            ? programTaskRepository.findByOnboardingProgram_ProgramId(programId)
            : programTaskRepository.findAll();

        // 2) 각 ProgramTask 에 대해 UserTask 매핑
        List<TaskInfo> tasks = programTasks.stream()
            .map(pt -> mapTask(userId, pt))
            .collect(Collectors.toList());

        // 3) 필터 적용
        tasks = tasks.stream()
            .filter(t -> statusFilter == null || t.getStatus().equals(statusFilter.name()))
            .filter(t -> overdue == null || (overdue && t.getDueDate().isBefore(LocalDate.now())))
            .collect(Collectors.toList());

        // 4) programId 계산 (옵션)
        Long resolvedProgramId = programId != null
            ? programId
            : (programTasks.isEmpty() ? null : programTasks.get(0).getOnboardingProgram().getProgramId());

        return UserTaskListResponse.builder()
            .userId(userId)
            .programId(resolvedProgramId)
            .tasks(tasks)
            .build();
    }

    private TaskInfo mapTask(Long userId, ProgramTask pt) {

        UserTask userTask = userTaskRepository
            .findByUser_UserIdAndProgramTask_Id(userId, pt.getId())
            .orElse(null);

        LocalDate dueDate = pt.getDueDate().toLocalDate();

        // 제출 기록 없음 → 자동 상태 계산
        if (userTask == null) {
            UserTaskStatus computed = dueDate.isBefore(LocalDate.now())
                ? UserTaskStatus.LATE
                : UserTaskStatus.PENDING;

            return TaskInfo.builder()
                .taskId(pt.getOnboardingTask().getId())
                .title(pt.getOnboardingTask().getTitle())
                .dueDate(dueDate)
                .status(computed.name())
                .grade(null)
                .submittedAt(null)
                .feedback(null)
                .build();
        }

        // 제출 기록 있음
        return TaskInfo.builder()
            .taskId(pt.getOnboardingTask().getId())
            .title(pt.getOnboardingTask().getTitle())
            .dueDate(dueDate)
            .status(userTask.getStatus().name())
            .grade(userTask.getGrade())
            .submittedAt(userTask.getSubmittedAt())
            .feedback(userTask.getFeedback())
            .build();
    }

    @Override
    public UserTaskScoreResponse getUserTaskScore(Long userId, Long programTaskId) {

        UserTask userTask = userTaskRepository
            .findByUser_UserIdAndProgramTask_Id(userId, programTaskId)
            .orElseThrow(() -> new IllegalArgumentException("UserTask not found"));

        ProgramTask programTask = userTask.getProgramTask();
        String title = programTask.getOnboardingTask().getTitle();
        Long taskId = programTask.getOnboardingTask().getId();

        return UserTaskScoreResponse.builder()
            .userId(userId)
            .taskId(taskId)
            .title(title)
            .grade(userTask.getGrade())
            .feedback(userTask.getFeedback())
            .updatedAt(userTask.getUpdatedAt())
            .build();
    }

}
