package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.response.AdminTaskDetailResponse;
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

        List<OnboardingTask> tasks = onboardingTaskRepository.findAll()
            .stream()
            .filter(task -> task.getIsDeleted() == null || !task.getIsDeleted())
            .toList();

        List<TaskListResponse.TaskSummary> taskSummaries = tasks.stream()
            .map(task -> {

                // ⭐ Task가 연결된 부서 ID 리스트 추출
                List<Long> deptIds = task.getTaskDepartments().stream()
                    .map(td -> td.getDepartment().getDepartmentId())
                    .toList();

                return TaskListResponse.TaskSummary.builder()
                    .taskId(task.getId())
                    .title(task.getTitle())
                    .description(task.getDescription())
                    .points(task.getPoints())
                    .createdAt(task.getCreatedAt())
                    .departmentIds(deptIds)     // ⭐ 추가된 필드
                    .build();
            })
            .toList();

        return TaskListResponse.builder()
            .totalCount(taskSummaries.size())
            .tasks(taskSummaries)
            .build();
    }

    @Override
    public AdminTaskDetailResponse getTaskDetail(Long taskId) {

        OnboardingTask task = onboardingTaskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (task.getIsDeleted() != null && task.getIsDeleted()) {
            throw new IllegalStateException("Task is deleted");
        }

        return AdminTaskDetailResponse.from(task);
    }
}