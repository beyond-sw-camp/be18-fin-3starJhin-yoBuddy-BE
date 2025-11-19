package com.j3s.yobuddy.domain.task.service;

import com.j3s.yobuddy.domain.task.dto.request.AdminTaskSearchCond;
import com.j3s.yobuddy.domain.task.dto.response.AdminTaskListResponse;
import org.springframework.data.domain.Pageable;

public interface TaskQueryService {
    AdminTaskListResponse getAdminTaskList(AdminTaskSearchCond cond, Pageable pageable);
}
