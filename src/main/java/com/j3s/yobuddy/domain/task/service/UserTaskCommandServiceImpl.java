package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.UserTaskSubmitRequest;
import com.j3s.yobuddy.domain.task.entity.UserTask;
import com.j3s.yobuddy.domain.task.repository.ProgramTaskRepository;
import com.j3s.yobuddy.domain.task.repository.UserTaskRepository;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserTaskCommandServiceImpl implements UserTaskCommandService {

    private final UserRepository userRepository;
    private final ProgramTaskRepository programTaskRepository;
    private final UserTaskRepository userTaskRepository;

    @Override
    public void submitTask(Long userId, Long programTaskId, UserTaskSubmitRequest request) {

        var user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var programTask = programTaskRepository.findById(programTaskId)
            .orElseThrow(() -> new IllegalArgumentException("ProgramTask not found"));

        // 기존 제출 기록 조회
        UserTask userTask = userTaskRepository
            .findByUser_UserIdAndProgramTask_Id(userId, programTaskId)
            .orElse(null);

        // 없으면 새로 생성
        if (userTask == null) {
            userTask = UserTask.builder()
                .user(user)
                .programTask(programTask)
                .build();
        }

        userTaskRepository.save(userTask);
    }
}
