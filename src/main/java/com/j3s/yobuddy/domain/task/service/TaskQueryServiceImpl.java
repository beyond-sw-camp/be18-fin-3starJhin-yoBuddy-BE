package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.AdminTaskSearchCond;
import com.j3s.yobuddy.domain.task.dto.response.AdminTaskListItem;
import com.j3s.yobuddy.domain.task.dto.response.AdminTaskListResponse;
import com.j3s.yobuddy.domain.task.entity.Task;
import com.j3s.yobuddy.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskQueryServiceImpl implements TaskQueryService {

    private final TaskRepository taskRepository;

    @Override
    public AdminTaskListResponse getAdminTaskList(AdminTaskSearchCond cond, Pageable pageable) {

        Page<Task> tasks = taskRepository.findAll(pageable); // 검색 조건 미구현 버전

        List<AdminTaskListItem> items = tasks.stream()
            .map(task -> AdminTaskListItem.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .points(task.getPoints())
                .assignedProgramCount(0)
                .createdAt(task.getCreatedAt())
                .build())
            .toList();

        return AdminTaskListResponse.builder()
            .totalCount((int) tasks.getTotalElements())
            .tasks(items)
            .build();
    }
}
