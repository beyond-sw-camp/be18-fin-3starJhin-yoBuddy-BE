package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskDetailResponse;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskListResponse;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskListResponse.TaskInfo;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskScoreResponse;
import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.entity.UserTaskStatus;
import com.j3s.yobuddy.domain.task.repository.ProgramTaskRepository;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserTaskQueryServiceImpl implements UserTaskQueryService {

    private final UserTaskRepository userTaskRepository;
    private final FileRepository fileRepository;

    @Override
    public UserTaskListResponse getUserTaskList(Long userId) {

        List<UserTask> tasks = userTaskRepository.findByUser_UserId(userId);

        var list = tasks.stream()
            .map(ut -> UserTaskListResponse.TaskInfo.builder()
                .userTaskId(ut.getId()) // ⭐ 진짜 userTaskId
                .taskId(ut.getProgramTask().getOnboardingTask().getId()) // ⭐ 실제 과제(Task) ID
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

    @Override
    public UserTaskDetailResponse getUserTaskDetail(Long userId, Long userTaskId) {

        UserTask ut = userTaskRepository
            .findByIdAndUser_UserId(userTaskId, userId)
            .orElseThrow(() -> new IllegalArgumentException("UserTask not found"));

        Long onboardingTaskId = ut.getProgramTask().getOnboardingTask().getId();

        // 1) 과제 기본 파일 (TASK)
        var taskFiles = fileRepository
            .findByRefTypeAndRefId(RefType.TASK, onboardingTaskId)
            .stream()
            .map(f -> UserTaskDetailResponse.FileInfo.builder()
                .fileId(f.getFileId())
                .fileName(f.getFilename())
                .filePath(f.getFilepath())
                .build())
            .toList();

        // 2) 유저 제출 파일 (USER_TASK)
        var submittedFiles = fileRepository
            .findByRefTypeAndRefId(RefType.USER_TASK, userTaskId)
            .stream()
            .map(f -> UserTaskDetailResponse.FileInfo.builder()
                .fileId(f.getFileId())
                .fileName(f.getFilename())
                .filePath(f.getFilepath())
                .build())
            .toList();

        return UserTaskDetailResponse.builder()
            .userTaskId(userTaskId)
            .taskId(onboardingTaskId)
            .title(ut.getProgramTask().getOnboardingTask().getTitle())
            .description(ut.getProgramTask().getOnboardingTask().getDescription())
            .dueDate(ut.getProgramTask().getDueDate().toLocalDate())
            .points(ut.getProgramTask().getOnboardingTask().getPoints())
            .status(ut.getStatus().name())
            .grade(ut.getGrade())
            .submittedAt(ut.getSubmittedAt())
            .updatedAt(ut.getUpdatedAt())
            .feedback(ut.getFeedback())
            .taskFiles(taskFiles)
            .submittedFiles(submittedFiles)
            .build();
    }

    @Override
    public UserTaskScoreResponse getUserTaskScore(Long userId, Long userTaskId) {

        UserTask ut = userTaskRepository
            .findByIdAndUser_UserId(userTaskId, userId)
            .orElseThrow(() -> new IllegalArgumentException("UserTask not found"));

        return UserTaskScoreResponse.builder()
            .userId(userId)
            .taskId(userTaskId)
            .title(ut.getProgramTask().getOnboardingTask().getTitle())
            .grade(ut.getGrade())
            .feedback(ut.getFeedback())
            .updatedAt(ut.getUpdatedAt())
            .build();
    }
}

