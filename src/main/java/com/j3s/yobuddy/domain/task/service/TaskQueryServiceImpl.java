package com.j3s.yobuddy.domain.task.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.task.dto.response.AdminTaskDetailResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskListResponse;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskListResponse;
import com.j3s.yobuddy.domain.task.entity.OnboardingTask;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.repository.OnboardingTaskRepository;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskQueryServiceImpl implements TaskQueryService {

    private final OnboardingTaskRepository onboardingTaskRepository;
    private final FileRepository fileRepository;
    private final UserTaskRepository userTaskRepository;

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

        List<FileResponse> files =
            fileRepository.findByRefTypeAndRefId(RefType.TASK, taskId).stream()
                .map(FileResponse::from)
                .toList();

        return AdminTaskDetailResponse.of(task, files);
    }
    @Override
    public UserTaskListResponse getUserTaskList(Long userId){
        List<UserTask> tasks = userTaskRepository.findByUser_UserId(userId);

        LocalDateTime now = LocalDateTime.now();
        for (UserTask ut : tasks) {
            ut.refreshMissingStatus(now);
        }

        userTaskRepository.saveAll(tasks);

        var list = tasks.stream()
            .map(ut -> UserTaskListResponse.TaskInfo.builder()
                .userTaskId(ut.getId())
                .taskId(ut.getProgramTask().getOnboardingTask().getId())
                .title(ut.getProgramTask().getOnboardingTask().getTitle())
                .dueDate(ut.getProgramTask().getDueDate().toLocalDate())
                .status(ut.getStatus().name())
                .grade(ut.getGrade())
                .submittedAt(ut.getSubmittedAt())
                .feedback(ut.getFeedback())
                .build())
            .toList();


        return UserTaskListResponse.builder()
            .userId(userId)
            .tasks(list)
            .build();
    }
}