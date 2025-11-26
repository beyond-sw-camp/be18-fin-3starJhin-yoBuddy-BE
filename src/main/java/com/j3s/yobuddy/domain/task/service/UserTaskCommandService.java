package com.j3s.yobuddy.domain.task.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface UserTaskCommandService {
    void submitTaskWithFiles(
        Long userId,
        Long userTaskId,
        List<Long> removeFileIds,
        List<MultipartFile> files
    ) throws Exception;
}
