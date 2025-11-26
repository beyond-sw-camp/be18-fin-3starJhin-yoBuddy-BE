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
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Override
    public TaskCreateResponse createTaskWithFiles(
        String title,
        String description,
        Integer points,
        List<Long> departmentIds,
        List<MultipartFile> files
    ) throws Exception {

        OnboardingTask task = OnboardingTask.builder()
            .title(title)
            .description(description)
            .points(points)
            .build();

        onboardingTaskRepository.save(task);

        // 부서 연결
        for (Long deptId : departmentIds) {
            Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

            TaskDepartment td = TaskDepartment.builder()
                .task(task)
                .department(dept)
                .build();

            task.getTaskDepartments().add(td);
            taskDepartmentRepository.save(td);
        }

        // 파일 업로드
        if (files != null) {
            for (MultipartFile file : files) {
                FileEntity uploaded = fileService.uploadTempFile(file, FileType.TASK);
                fileService.bindFile(uploaded.getFileId(), RefType.TASK, task.getId());
            }
        }

        List<FileResponse> attached = fileRepository
            .findByRefTypeAndRefId(RefType.TASK, task.getId())
            .stream()
            .map(FileResponse::from)
            .toList();

        return TaskCreateResponse.of(task, attached);
    }

    @Override
    public TaskUpdateResponse updateTaskWithFiles(
        Long taskId,
        String title,
        String description,
        Integer points,
        List<Long> departmentIds,
        List<Long> removeFileIds,
        List<MultipartFile> files
    ) throws Exception {

        OnboardingTask task = onboardingTaskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (title != null) task.setTitle(title);
        if (description != null) task.setDescription(description);
        if (points != null) task.setPoints(points);

        // 부서 재매핑
        if (departmentIds != null) {
            task.getTaskDepartments().clear();

            for (Long deptId : departmentIds) {
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

        // 파일 삭제
        if (removeFileIds != null) {
            for (Long fileId : removeFileIds) {
                FileEntity file = fileService.getFileEntity(fileId);
                file.setRefType(null);
                file.setRefId(null);
                fileRepository.save(file);
            }
        }

        // 새 파일 업로드
        if (files != null) {
            for (MultipartFile file : files) {
                FileEntity uploaded = fileService.uploadTempFile(file, FileType.TASK);
                fileService.bindFile(uploaded.getFileId(), RefType.TASK, task.getId());
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
