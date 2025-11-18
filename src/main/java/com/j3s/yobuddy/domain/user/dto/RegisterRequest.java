package com.j3s.yobuddy.domain.user.dto;

import java.time.LocalDateTime;

import com.j3s.yobuddy.domain.user.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
public class RegisterRequest {
    private final String name;
    private final String email;
    private final String password;
    private final String phoneNumber;
    private final Role role;
    private final LocalDateTime joinedAt;
    private final Long departmentId;
    private final String position;
}
