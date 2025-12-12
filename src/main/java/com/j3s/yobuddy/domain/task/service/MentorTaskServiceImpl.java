package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.file.entity.RefType;
import com.j3s.yobuddy.domain.file.repository.FileRepository;
import com.j3s.yobuddy.domain.mentor.menteeAssignment.repository.MentorMenteeAssignmentRepository;
import com.j3s.yobuddy.domain.task.dto.request.TaskGradeRequest;
import com.j3s.yobuddy.domain.task.dto.response.MentorTaskDetailResponse;
import com.j3s.yobuddy.domain.task.dto.response.MentorTaskListResponse;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;
import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.user.entity.Role;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MentorTaskServiceImpl implements MentorTaskService {

    private final MentorMenteeAssignmentRepository assignmentRepository;
    private final UserTaskRepository userTaskRepository;
    private final FileRepository fileRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public MentorTaskListResponse getAllMenteeTasks(Long mentorId) {

        List<Long> menteeIds = assignmentRepository.findMenteeIdsByMentorUserId(mentorId);

        if (menteeIds.isEmpty()) {
            return MentorTaskListResponse.builder()
                .mentorId(mentorId)
                .tasks(List.of())
                .build();
        }

        List<UserTask> menteeTasks = userTaskRepository.findByUser_UserIdIn(menteeIds);

        LocalDateTime now = LocalDateTime.now();
        for (UserTask ut : menteeTasks) {
            ut.refreshMissingStatus(now);
        }

        userTaskRepository.saveAll(menteeTasks);

        List<MentorTaskListResponse.MenteeTaskInfo> taskInfos =
            menteeTasks.stream().map(ut -> {
                    var mentee = ut.getUser();
                    var pt = ut.getProgramTask();
                    var t = pt.getOnboardingTask();

                    return MentorTaskListResponse.MenteeTaskInfo.builder()
                        .userTaskId(ut.getId())
                        .menteeId(mentee.getUserId())
                        .menteeName(mentee.getName())
                        .onboardingTaskId(t.getId())
                        .taskTitle(t.getTitle())
                        .dueDate(pt.getDueDate().toLocalDate())
                        .status(ut.getStatus().name())
                        .grade(ut.getGrade())
                        .submittedAt(ut.getSubmittedAt())
                        .feedback(ut.getFeedback())
                        .build();
                })
                .collect(Collectors.toList());

        return MentorTaskListResponse.builder()
            .mentorId(mentorId)
            .tasks(taskInfos)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MentorTaskDetailResponse getTaskDetail(Long mentorId, Long userTaskId) {

        UserTask ut = userTaskRepository.findById(userTaskId)
            .orElseThrow(() -> new IllegalArgumentException("UserTask not found"));

        Long menteeId = ut.getUser().getUserId();

        boolean assigned = assignmentRepository
            .existsByMentorUserIdAndMenteeUserId(mentorId, menteeId);

        if (!assigned) {
            throw new IllegalStateException("You are not assigned to this mentee.");
        }

        var pt = ut.getProgramTask();
        var task = pt.getOnboardingTask();

        var taskFiles = fileRepository.findByRefTypeAndRefId(RefType.TASK, task.getId())
            .stream()
            .map(f -> MentorTaskDetailResponse.FileInfo.builder()
                .fileId(f.getFileId())
                .fileName(f.getFilename())
                .filePath(f.getFilepath())
                .build())
            .toList();

        var submittedFiles = fileRepository.findByRefTypeAndRefId(RefType.USER_TASK, userTaskId)
            .stream()
            .map(f -> MentorTaskDetailResponse.FileInfo.builder()
                .fileId(f.getFileId())
                .fileName(f.getFilename())
                .filePath(f.getFilepath())
                .build())
            .toList();

        return MentorTaskDetailResponse.builder()
            .userTaskId(ut.getId())
            .menteeId(menteeId)
            .menteeName(ut.getUser().getName())
            .taskId(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .points(task.getPoints())
            .dueDate(pt.getDueDate().toLocalDate())
            .status(ut.getStatus().name())
            .grade(ut.getGrade())
            .submittedAt(ut.getSubmittedAt())
            .feedback(ut.getFeedback())
            .updatedAt(ut.getUpdatedAt())
            .taskFiles(taskFiles)
            .submittedFiles(submittedFiles)
            .build();
    }

    @Override
    public void gradeTask(Long mentorId, Long userTaskId, TaskGradeRequest request) {

        UserTask ut = userTaskRepository.findById(userTaskId)
            .orElseThrow(() -> new IllegalArgumentException("UserTask not found"));

        Long menteeId = ut.getUser().getUserId();

        boolean assigned = assignmentRepository
            .existsByMentorUserIdAndMenteeUserId(mentorId, menteeId);

        if (!assigned) {
            throw new IllegalStateException("You are not assigned to this mentee.");
        }

        ut.grade(request.getGrade(), request.getFeedback());

        if (!ut.getUser().isDeleted() && ut.getUser().getRole() == Role.USER) {
            notificationService.notify(
                ut.getUser(),
                NotificationType.TASK_GRADED,
                "과제 채점 완료",
                "제출한 과제 채점이 완료되었어요."
            );
        }
    }
}

