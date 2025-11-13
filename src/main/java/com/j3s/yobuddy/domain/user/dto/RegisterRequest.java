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
@NoArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private Role role;
    private LocalDateTime joinedAt;
    private Long departmentId;
    private String position;
}
