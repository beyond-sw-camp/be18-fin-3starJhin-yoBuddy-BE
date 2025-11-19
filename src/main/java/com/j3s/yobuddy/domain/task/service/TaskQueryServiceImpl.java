package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.response.TaskListResponse;
import com.j3s.yobuddy.domain.task.entity.OnboardingTask;
import com.j3s.yobuddy.domain.task.repository.OnboardingTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskQueryServiceImpl implements TaskQueryService {

    private final OnboardingTaskRepository onboardingTaskRepository;

    @Override
    public TaskListResponse getTaskList() {

        // Soft Delete 되지 않은 Task 만 조회
        List<OnboardingTask> tasks = onboardingTaskRepository.findAll()
            .stream()
            .filter(task -> task.getIsDeleted() == null || !task.getIsDeleted())
            .toList();

        List<TaskListResponse.TaskSummary> taskSummaries = tasks.stream()
            .map(task -> TaskListResponse.TaskSummary.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .points(task.getPoints())
                .createdAt(task.getCreatedAt())
                .build()
            )
            .toList();

        return TaskListResponse.builder()
            .totalCount(taskSummaries.size())
            .tasks(taskSummaries)
            .build();
    }
}
