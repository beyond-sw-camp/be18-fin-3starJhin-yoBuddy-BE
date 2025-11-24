package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.TaskCreateRequest;
import com.j3s.yobuddy.domain.task.dto.request.TaskUpdateRequest;
import com.j3s.yobuddy.domain.task.dto.response.TaskCreateResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskDeleteResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskUpdateResponse;
import com.j3s.yobuddy.domain.task.entity.OnboardingTask;
import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import com.j3s.yobuddy.domain.task.repository.OnboardingTaskRepository;
import com.j3s.yobuddy.domain.task.repository.ProgramTaskRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskCommandServiceImpl implements TaskCommandService {

    private final OnboardingTaskRepository onboardingTaskRepository;
    private final ProgramTaskRepository programTaskRepository;


    @Override
    public TaskCreateResponse createTask(TaskCreateRequest request) {

        OnboardingTask task = OnboardingTask.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .points(request.getPoints())
            .build();

        OnboardingTask saved = onboardingTaskRepository.save(task);

        return TaskCreateResponse.builder()
            .taskId(saved.getId())
            .title(saved.getTitle())
            .description(saved.getDescription())
            .points(saved.getPoints())
            .createdAt(saved.getCreatedAt())
            .updatedAt(saved.getCreatedAt())
            .build();
    }

    @Override
    @Transactional
    public TaskUpdateResponse updateTask(Long taskId, TaskUpdateRequest request){

        OnboardingTask task = onboardingTaskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getPoints() != null) task.setPoints(request.getPoints());
        if (request.getFileIds() != null) {
            // 필요하면 파일 처리 로직
        }

        task.setUpdatedAt(LocalDateTime.now());

        return TaskUpdateResponse.builder()
            .taskId(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .points(task.getPoints())
            .updatedAt(task.getUpdatedAt())
            .build();
    }


    @Override
    @Transactional
    public TaskDeleteResponse deleteTask(Long taskId) {

        OnboardingTask task = onboardingTaskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        int programUnlinkedCount = programTaskRepository.countByOnboardingTaskId(taskId);

        // 3) Soft delete 처리
        task.delete();
        task.setUpdatedAt(LocalDateTime.now());

        programTaskRepository.deleteByOnboardingTaskId(taskId);

        return TaskDeleteResponse.builder()
            .message("Task deleted successfully")
            .taskId(task.getId())
            .deletedAt(LocalDateTime.now())
            .relatedEntities(
                TaskDeleteResponse.RelatedEntities.builder()
                    .programUnlinkedCount(programUnlinkedCount)
                    .userTaskRemovedCount(0)
                    .build()
            )
            .build();
    }
}
