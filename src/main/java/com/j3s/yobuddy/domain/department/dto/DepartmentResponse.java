package com.j3s.yobuddy.domain.department.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DepartmentResponse {

    private final Long departmentId;
    private final String name;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
