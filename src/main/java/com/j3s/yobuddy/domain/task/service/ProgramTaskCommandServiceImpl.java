package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.notification.entity.NotificationType;
import com.j3s.yobuddy.domain.notification.service.NotificationService;
import com.j3s.yobuddy.domain.onboarding.repository.OnboardingProgramRepository;
import com.j3s.yobuddy.domain.programenrollment.entity.ProgramEnrollment;
import com.j3s.yobuddy.domain.programenrollment.repository.ProgramEnrollmentRepository;
import com.j3s.yobuddy.domain.task.dto.request.ProgramTaskAssignRequest;
import com.j3s.yobuddy.domain.task.dto.response.ProgramTaskAssignResponse;
import com.j3s.yobuddy.domain.task.entity.OnboardingTask;
import com.j3s.yobuddy.domain.task.entity.ProgramTask;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.repository.OnboardingTaskRepository;
import com.j3s.yobuddy.domain.task.repository.ProgramTaskRepository;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramTaskCommandServiceImpl implements ProgramTaskCommandService {

    private final OnboardingProgramRepository programRepository;
    private final OnboardingTaskRepository taskRepository;
    private final ProgramTaskRepository programTaskRepository;
    private final UserTaskRepository userTaskRepository;

    private final ProgramEnrollmentRepository enrollmentRepository;
    private final UserTaskAssignmentService userTaskAssignmentService;
    private final NotificationService notificationService;

    @Override
    public ProgramTaskAssignResponse assignTask(Long programId, Long taskId,
        ProgramTaskAssignRequest request) {

        var program = programRepository.findById(programId)
            .orElseThrow(() -> new IllegalArgumentException("프로그램을 찾을 수 없습니다."));

        var task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("과제를 찾을 수 없습니다."));

        if (programTaskRepository.existsByOnboardingProgram_ProgramIdAndOnboardingTask_Id(programId,
            taskId)) {
            throw new IllegalStateException("이미 해당 프로그램에 등록된 과제입니다.");
        }

        LocalDateTime dueDateTime = request.getDueDate().atTime(23, 59, 59);

        ProgramTask programTask = ProgramTask.builder()
            .onboardingProgram(program)
            .onboardingTask(task)
            .dueDate(dueDateTime)
            .build();

        ProgramTask saved = programTaskRepository.save(programTask);

        List<User> enrolledUsers = enrollmentRepository.findByProgram_ProgramId(programId)
            .stream()
            .map(ProgramEnrollment::getUser)
            .toList();

        userTaskAssignmentService.assignForProgramTask(saved, enrolledUsers);

        notifyNewProgramTask(task, enrolledUsers);

        return ProgramTaskAssignResponse.of(saved);
    }

    @Override
    public void unassignTask(Long programId, Long taskId) {

        ProgramTask programTask = programTaskRepository
            .findByOnboardingProgram_ProgramIdAndOnboardingTask_Id(programId, taskId)
            .orElseThrow(() -> new IllegalArgumentException("프로그램 과제를 찾을 수 없습니다."));

        List<UserTask> userTasks = userTaskRepository
            .findAllByProgramTaskAndDeletedFalse(programTask);

        if (!userTasks.isEmpty()) {
            userTaskRepository.deleteAll(userTasks);
        }

        programTaskRepository.delete(programTask);
    }

    @Override
    public void notifyNewProgramTask(OnboardingTask task, List<User> enrolledUsers) {
        Set<User> mentees = enrolledUsers.stream()
            .filter(user -> user.getRole() == Role.USER && !user.isDeleted())
            .collect(Collectors.toSet());

        final String message =
            (task != null && task.getTitle() != null && !task.getTitle().isBlank())
                ? "새롭게 등록된 과제가 있어요. (" + task.getTitle() + ")"
                : "새롭게 등록된 과제가 있어요.";

        mentees.forEach(user -> notificationService.notify(
            user,
            NotificationType.NEW_PROGRAM_TASK,
            "새로운 과제 등록",
            message
        ));
    }
}
