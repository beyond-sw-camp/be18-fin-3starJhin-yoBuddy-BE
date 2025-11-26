package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.file.service.FileService;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.repository.ProgramTaskRepository;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;
import com.j3s.yobuddy.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserTaskCommandServiceImpl implements UserTaskCommandService {

    private final UserTaskRepository userTaskRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;

    @Override
    public void submitTaskWithFiles(
        Long userId,
        Long userTaskId,
        List<Long> removeFileIds,
        List<MultipartFile> files
    ) throws Exception {

        UserTask userTask = userTaskRepository
            .findByIdAndUser_UserId(userTaskId, userId)
            .orElseThrow(() -> new IllegalArgumentException("UserTask not found"));

        // 상태 업데이트
        userTask.submit();
        userTaskRepository.save(userTask);

        // 파일 삭제
        if (removeFileIds != null) {
            for (Long id : removeFileIds) {
                FileEntity file = fileService.getFileEntity(id);
                file.setRefType(null);
                file.setRefId(null);
                fileRepository.save(file);
            }
        }

        // 새 파일 업로드
        if (files != null) {
            for (MultipartFile file : files) {
                FileEntity uploaded = fileService.uploadTempFile(file, FileType.USER_TASK);
                fileService.bindFile(uploaded.getFileId(), RefType.USER_TASK, userTaskId);
            }
        }
    }
}

