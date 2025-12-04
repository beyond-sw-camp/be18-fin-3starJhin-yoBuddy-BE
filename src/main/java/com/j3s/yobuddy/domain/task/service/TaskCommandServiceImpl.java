// file: com/j3s/yobuddy/domain/task/service/TaskCommandServiceImpl.java
package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.common.dto.FileResponse;
import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.department.repository.DepartmentRepository;
import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.file.service.FileService;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskCommandServiceImpl implements TaskCommandService {

    private final OnboardingTaskRepository onboardingTaskRepository;
    private final DepartmentRepository departmentRepository;
    private final TaskDepartmentRepository taskDepartmentRepository;
    private final ProgramTaskRepository programTaskRepository;

    private final FileService fileService;
    private final FileRepository fileRepository;

    /**
     * 🔥 JSON + fileIds 기반 과제 생성
     */
    @Override
    public TaskCreateResponse createTask(TaskCreateRequest request) throws Exception {

        OnboardingTask task = OnboardingTask.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .points(request.getPoints())
            .build();

        onboardingTaskRepository.save(task);

        // 부서 연결
        if (request.getDepartmentIds() != null) {
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

        // 🔥 파일 매핑 (이미 업로드된 fileId들을 Task에 연결)
        if (request.getFileIds() != null) {
            for (Long fileId : request.getFileIds()) {
                fileService.bindFile(fileId, RefType.TASK, task.getId());
            }
        }

        List<FileResponse> attached = fileRepository
            .findByRefTypeAndRefId(RefType.TASK, task.getId())
            .stream()
            .map(FileResponse::from)
            .toList();

        return TaskCreateResponse.of(task, attached);
    }

    /**
     * 🔥 JSON + fileIds 기반 과제 수정
     */
    @Override
    public TaskUpdateResponse updateTask(Long taskId, TaskUpdateRequest request) throws Exception {

        OnboardingTask task = onboardingTaskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // 필드 부분 수정
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPoints() != null) {
            task.setPoints(request.getPoints());
        }

        // 부서 재매핑
        if (request.getDepartmentIds() != null) {
            task.getTaskDepartments().clear();

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

        // 🔥 파일 연결 해제
        if (request.getRemoveFileIds() != null) {
            for (Long fileId : request.getRemoveFileIds()) {
                FileEntity file = fileService.getFileEntity(fileId);
                file.setRefType(null);
                file.setRefId(null);
                fileRepository.save(file);
            }
        }

        // 🔥 새로 연결할 파일들 (이미 업로드된 fileId 기준)
        if (request.getFileIds() != null) {
            for (Long fileId : request.getFileIds()) {
                fileService.bindFile(fileId, RefType.TASK, task.getId());
            }
        }

        List<FileResponse> attached = fileRepository
            .findByRefTypeAndRefId(RefType.TASK, task.getId())
            .stream()
            .map(FileResponse::from)
            .toList();

        return TaskUpdateResponse.of(task, attached);
    }

    @Override
    public TaskDeleteResponse deleteTask(Long taskId) {

        OnboardingTask task = onboardingTaskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        int programUnlinkedCount =
            programTaskRepository.countByOnboardingTaskId(taskId);

        task.delete();
        task.setUpdatedAt(LocalDateTime.now());

        // 파일 연결 해제
        List<FileEntity> files = fileRepository.findByRefTypeAndRefId(RefType.TASK, taskId);
        for (FileEntity file : files) {
            file.setRefType(null);
            file.setRefId(null);
            fileRepository.save(file);
        }

        return TaskDeleteResponse.of(taskId, programUnlinkedCount);
    }
}
