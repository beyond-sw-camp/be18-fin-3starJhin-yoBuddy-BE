package com.j3s.yobuddy.domain.user.dto;

import com.j3s.yobuddy.domain.user.entity.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSearchRequest {
    private String name;
    private String email;
    private Role role;
}