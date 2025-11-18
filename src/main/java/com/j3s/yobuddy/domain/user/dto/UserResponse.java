package com.j3s.yobuddy.domain.user.dto;

import java.time.LocalDateTime;

import com.j3s.yobuddy.domain.department.entity.Department;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    private final Long userId;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final Role role;
    private final Long departmentId;
    private final String departmentName;
    private final LocalDateTime joinedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Boolean isDeleted;

    public static UserResponse from(User u) {
        if (u == null) return null;
        Department d = u.getDepartment();
        return UserResponse.builder()
            .userId(u.getUserId())
            .name(u.getName())
            .email(u.getEmail())
            .phoneNumber(u.getPhoneNumber())
            .role(u.getRole())
            .departmentId(d != null ? d.getDepartmentId() : null)
            .departmentName(d != null ? d.getName() : null)
            .joinedAt(u.getJoinedAt())
            .createdAt(u.getCreatedAt())
            .updatedAt(u.getUpdatedAt())
            .isDeleted(u.isDeleted())
            .build();
    }
}
