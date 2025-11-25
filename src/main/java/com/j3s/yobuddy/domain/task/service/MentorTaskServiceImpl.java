package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.mentor.repository.MentorMenteeAssignmentRepository;
import com.j3s.yobuddy.domain.task.dto.request.TaskGradeRequest;
import com.j3s.yobuddy.domain.task.dto.response.MentorMenteeTaskResponse;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.repository.ProgramTaskRepository;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;
import com.j3s.yobuddy.domain.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MentorTaskServiceImpl implements MentorTaskService {

    private final MentorMenteeAssignmentRepository mentorMenteeAssignmentRepository;
    private final UserRepository userRepository;
    private final ProgramTaskRepository programTaskRepository;
    private final UserTaskRepository userTaskRepository;

    @Override
    public void gradeTask(Long mentorId, Long menteeId, Long programTaskId, TaskGradeRequest request) {

        // 1) 멘토-멘티 관계 검증
        boolean assigned = mentorMenteeAssignmentRepository
            .existsByMentorUserIdAndMenteeUserIdAndDeletedFalse(mentorId, menteeId);

        if (!assigned) {
            throw new IllegalStateException("You are not assigned as a mentor of this mentee.");
        }

        // 2) 멘티 존재 확인
        userRepository.findById(menteeId)
            .orElseThrow(() -> new IllegalArgumentException("Mentee not found"));

        // 3) ProgramTask 존재 확인
        programTaskRepository.findById(programTaskId)
            .orElseThrow(() -> new IllegalArgumentException("ProgramTask not found"));

        // 4) 멘티의 제출(UserTask) 조회
        UserTask userTask = userTaskRepository
            .findByUser_UserIdAndProgramTask_Id(menteeId, programTaskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not submitted by mentee"));

        // 5) 채점 반영 (엔티티 도메인 메서드)
        userTask.grade(request.getGrade(), request.getFeedback());

        // ⚠ save() 필요 없음 — Dirty Checking 자동 적용됨
    }

    @Override
    @Transactional(readOnly = true)
    public MentorMenteeTaskResponse getSubmittedTasks(Long mentorId, Long menteeId) {

        // 1) 멘토-멘티 관계 검증
        boolean assigned = mentorMenteeAssignmentRepository
            .existsByMentorUserIdAndMenteeUserIdAndDeletedFalse(mentorId, menteeId);

        if (!assigned) {
            throw new IllegalStateException("You are not assigned as a mentor of this mentee.");
        }

        // 2) 멘티 제출 과제(UserTask) 조회
        List<UserTask> submitted = userTaskRepository.findByUser_UserId(menteeId);

        // 3) DTO 변환
        List<MentorMenteeTaskResponse.TaskInfo> taskInfos = submitted.stream()
            .map(ut -> {
                var pt = ut.getProgramTask();
                var t = pt.getOnboardingTask();

                return MentorMenteeTaskResponse.TaskInfo.builder()
                    .programTaskId(pt.getId())
                    .taskId(t.getId())
                    .title(t.getTitle())
                    .dueDate(pt.getDueDate().toLocalDate())
                    .status(ut.getStatus().name())
                    .grade(ut.getGrade())
                    .submittedAt(ut.getSubmittedAt())
                    .feedback(ut.getFeedback())
                    .build();
            })
            .collect(Collectors.toList());

        return MentorMenteeTaskResponse.builder()
            .menteeId(menteeId)
            .tasks(taskInfos)
            .build();
    }

}
