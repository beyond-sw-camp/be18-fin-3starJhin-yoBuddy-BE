package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.file.entity.FileEntity;
import com.j3s.yobuddy.domain.file.entity.FileType;
import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.file.service.FileService;
import com.j3s.yobuddy.domain.mentor.menteeAssignment.repository.MentorMenteeAssignmentRepository;
import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.task.dto.request.TaskSubmitRequest;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;
import com.j3s.yobuddy.domain.user.entity.Role;

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
    private final NotificationService notificationService;
    private final MentorMenteeAssignmentRepository mentorMenteeAssignmentRepository;

    @Override
    public void submitTaskWithFiles(
        Long userId,
        Long userTaskId,
        TaskSubmitRequest request
    ) throws Exception {

        UserTask userTask = userTaskRepository
            .findByIdAndUser_UserId(userTaskId, userId)
            .orElseThrow(() -> new IllegalArgumentException("UserTask not found"));

        // 🔥 제출 + 코멘트 저장
        userTask.submit(request.getComment());
        userTaskRepository.save(userTask);

        mentorMenteeAssignmentRepository.findByMenteeUserIdAndDeletedFalse(userId)
            .map(assignment -> assignment.getMentor())
            .filter(mentor -> !mentor.isDeleted() && mentor.getRole() == Role.MENTOR)
            .ifPresent(mentor -> notificationService.notify(
                mentor,
                NotificationType.MENTOR_TASK_SUBMITTED,
                "과제 제출 알림",
                "제출된 과제가 있어요. 채점해 주세요."
            ));

        // 🔥 기존 파일 전체 제거 처리
        List<FileEntity> existingFiles =
            fileRepository.findByRefTypeAndRefId(RefType.USER_TASK, userTaskId);

        for (FileEntity file : existingFiles) {
            file.setRefType(null);
            file.setRefId(null);
            fileRepository.save(file);
        }

        // 🔥 새 파일 업로드
        if (request.getFiles() != null) {
            for (MultipartFile file : request.getFiles()) {
                FileEntity uploaded = fileService.uploadTempFile(file, FileType.USER_TASK);
                fileService.bindFile(uploaded.getFileId(), RefType.USER_TASK, userTaskId);
            }
        }
    }
}
