package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.department.repository.DepartmentRepository;
import com.j3s.yobuddy.domain.task.dto.request.TaskCreateRequest;
import com.j3s.yobuddy.domain.task.dto.request.TaskUpdateRequest;
import com.j3s.yobuddy.domain.task.dto.response.TaskCreateResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskDeleteResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskUpdateResponse;
import com.j3s.yobuddy.domain.task.entity.OnboardingTask;
import com.j3s.yobuddy.domain.task.entity.TaskDepartment;
import com.j3s.yobuddy.domain.task.repository.TaskDepartmentRepository;
import com.j3s.yobuddy.domain.task.repository.OnboardingTaskRepository;
import com.j3s.yobuddy.domain.task.repository.ProgramTaskRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskCommandServiceImpl implements TaskCommandService {

    private final OnboardingTaskRepository onboardingTaskRepository;
    private final ProgramTaskRepository programTaskRepository;
    private final DepartmentRepository departmentRepository;
    private final TaskDepartmentRepository taskDepartmentRepository;

    // ---------------------
    // CREATE TASK
    // ---------------------
    @Override
    @Transactional
    public TaskCreateResponse createTask(TaskCreateRequest request) {

        OnboardingTask task = OnboardingTask.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .points(request.getPoints())
            .build();

        onboardingTaskRepository.save(task);

        List<Long> validDepartmentIds = new ArrayList<>();

        for (Long deptId : request.getDepartmentIds()) {

            Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

            if (Boolean.TRUE.equals(dept.getIsDeleted())) {
                throw new IllegalArgumentException("Department " + deptId + " is deleted.");
            }

            TaskDepartment td = TaskDepartment.builder()
                .task(task)
                .department(dept)
                .build();

            task.getTaskDepartments().add(td);
            validDepartmentIds.add(dept.getDepartmentId());
        }

        return TaskCreateResponse.builder()
            .taskId(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .points(task.getPoints())
            .departmentIds(validDepartmentIds)  // ⭐ 추가!
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .build();
    }


    // ---------------------
    // UPDATE TASK
    // ---------------------
    @Override
    @Transactional
    public TaskUpdateResponse updateTask(Long taskId, TaskUpdateRequest request) {

        // 1) 기존 Task 조회
        OnboardingTask task = onboardingTaskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // 2) 필드 수정
        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getPoints() != null) task.setPoints(request.getPoints());

        task.setUpdatedAt(LocalDateTime.now());

        // 3) 부서 변경처리
        if (request.getDepartmentIds() != null) {

            // (1) 기존 TaskDepartment 제거 (orphanRemoval 때문에 자동 삭제됨)
            task.getTaskDepartments().clear();

            // (2) 새 부서 매핑 입력
            for (Long deptId : request.getDepartmentIds()) {
                Department dept = departmentRepository.findById(deptId)
                    .orElseThrow(() -> new IllegalArgumentException("Department not found"));

                TaskDepartment td = TaskDepartment.builder()
                    .task(task)
                    .department(dept)
                    .build();

                task.getTaskDepartments().add(td);
                taskDepartmentRepository.save(td);
            }
        }

        return TaskUpdateResponse.builder()
            .taskId(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .points(task.getPoints())
            .updatedAt(task.getUpdatedAt())
            .build();
    }

    // ---------------------
    // DELETE TASK
    // ---------------------
    @Override
    @Transactional
    public TaskDeleteResponse deleteTask(Long taskId) {

        OnboardingTask task = onboardingTaskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        int programUnlinkedCount = programTaskRepository.countByOnboardingTaskId(taskId);

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
