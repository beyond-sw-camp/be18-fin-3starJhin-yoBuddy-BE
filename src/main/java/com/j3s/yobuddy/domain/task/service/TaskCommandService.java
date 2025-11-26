package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.TaskCreateRequest;
import com.j3s.yobuddy.domain.task.dto.request.TaskUpdateRequest;
import com.j3s.yobuddy.domain.task.dto.response.TaskCreateResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskDeleteResponse;
import com.j3s.yobuddy.domain.task.dto.response.TaskUpdateResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface TaskCommandService {
    TaskCreateResponse createTaskWithFiles(
        String title,
        String description,
        Integer points,
        List<Long> departmentIds,
        List<MultipartFile> files
    ) throws Exception;

    TaskUpdateResponse updateTaskWithFiles(
        Long taskId,
        String title,
        String description,
        Integer points,
        List<Long> departmentIds,
        List<Long> removeFileIds,
        List<MultipartFile> files
    ) throws Exception;

    TaskDeleteResponse deleteTask(Long taskId);


}
