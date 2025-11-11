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
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
    private Long departmentId;
    private String departmentName;
    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

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
