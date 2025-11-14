package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.TaskCreateRequest;
import com.j3s.yobuddy.domain.task.dto.request.TaskUpdateRequest;
import com.j3s.yobuddy.domain.task.dto.response.TaskCreateResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskDeleteResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskUpdateResponse;
import com.j3s.yobuddy.domain.task.entity.Task;
import com.j3s.yobuddy.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskCommandServiceImpl implements TaskCommandService {

    private final TaskRepository taskRepository;

    @Override
    public TaskCreateResponse createTask(TaskCreateRequest request) {

        Task task = Task.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .dueDate(request.getDueDate())
            .points(request.getPoints())
            .status("ACTIVE")
            .build();

        Task saved = taskRepository.save(task);

        return TaskCreateResponse.builder()
            .taskId(saved.getId())
            .title(saved.getTitle())
            .description(saved.getDescription())
            .dueDate(saved.getDueDate())
            .points(saved.getPoints())
            .createdAt(saved.getCreatedAt())
            .updatedAt(saved.getCreatedAt())
            .build();
    }

    @Override
    public TaskUpdateResponse updateTask(Long taskId, TaskUpdateRequest request) {

        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // 들어온 값만 업데이트 (partial update)
        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getPoints() != null) task.setPoints(request.getPoints());

        // 업데이트 시각 기록
        task.setUpdatedAt(LocalDateTime.now());

        Task updated = taskRepository.save(task);

        return TaskUpdateResponse.builder()
            .taskId(updated.getId())
            .title(updated.getTitle())
            .description(updated.getDescription())
            .dueDate(updated.getDueDate())
            .points(updated.getPoints())
            .updatedAt(updated.getUpdatedAt())
            .build();
    }

    @Override
    public TaskDeleteResponse deleteTask(Long taskId) {

        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        int programUnlinkedCount = 0;
        int userTaskRemovedCount = 0;

        // 실제 삭제
        taskRepository.delete(task);

        return TaskDeleteResponse.builder()
            .taskId(taskId)
            .deletedAt(LocalDateTime.now())
            .relatedEntities(
                TaskDeleteResponse.RelatedEntities.builder()
                    .programUnlinkedCount(programUnlinkedCount)
                    .userTaskRemovedCount(userTaskRemovedCount)
                    .build()
            )
            .build();
    }

}
