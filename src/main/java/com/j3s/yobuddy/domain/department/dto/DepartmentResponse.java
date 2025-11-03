package com.j3s.yobuddy.domain.department.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DepartmentResponse {

    private final Long departmentId;
    private final String name;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
