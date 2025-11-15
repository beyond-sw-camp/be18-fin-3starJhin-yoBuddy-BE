package com.j3s.yobuddy.domain.task.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminTaskListResponse {
    private int totalCount;
    private List<AdminTaskListItem> tasks;
}
