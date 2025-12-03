package com.j3s.yobuddy.domain.task.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskDetailResponse;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskListResponse;
import com.j3s.yobuddy.domain.task.dto.response.UserTaskScoreResponse;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.entity.UserTaskStatus;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserTaskQueryServiceImpl implements UserTaskQueryService {

    private final UserTaskRepository userTaskRepository;
    private final FileRepository fileRepository;

    @Override
    @Transactional
    public UserTaskListResponse getUserTaskList(Long userId) {

        List<UserTask> tasks = userTaskRepository.findByUser_UserId(userId);

        LocalDateTime now = LocalDateTime.now();
        for (UserTask ut : tasks) {
            ut.refreshMissingStatus(now);
        }

        userTaskRepository.saveAll(tasks);

        var list = tasks.stream()
            .map(ut -> UserTaskListResponse.TaskInfo.builder()
                .userTaskId(ut.getId())
                .taskId(ut.getProgramTask().getOnboardingTask().getId())
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
            .comment(ut.getComment())
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
    @Override
    public BigDecimal calculateCompletionRate(Long userId) {
        var resp = getUserTaskList(userId);
        var tasks = resp.getTasks();
        long total = tasks.size();
        long completed = tasks.stream()
            .filter(t -> {
            String s = t.getStatus();
            return s != null && (s.equals(UserTaskStatus.GRADED.name())
                || s.equals(UserTaskStatus.LATE.name())
                || s.equals(UserTaskStatus.SUBMITTED.name()));
            })
            .count();

        if (total == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal rate = BigDecimal.valueOf((completed / total)* 100.0);
        return rate;
    }
    @Override
    public BigDecimal calculateTaskScore(Long userId) {
        var resp = getUserTaskList(userId);
        var tasks = resp.getTasks();
        var gradedGrades = tasks.stream()
            .filter(t -> {
            String s = t.getStatus();
            return s != null && s.equals(UserTaskStatus.GRADED.name()) && t.getGrade() != null;
            })
            .map(t -> t.getGrade())
            .toList();

        if (gradedGrades.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int sum = gradedGrades.stream()
            .mapToInt(Integer::intValue)
            .sum();

        BigDecimal divisor = BigDecimal.valueOf(gradedGrades.size());
        BigDecimal score = BigDecimal.valueOf(sum).divide(divisor, 2, java.math.RoundingMode.HALF_UP);
        return score;
    }
}

