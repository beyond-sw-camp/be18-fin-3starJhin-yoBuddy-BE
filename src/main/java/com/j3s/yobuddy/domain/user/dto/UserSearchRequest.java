package com.j3s.yobuddy.domain.user.dto;

import com.j3s.yobuddy.domain.user.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class UserSearchRequest {
    private final String name;
    private final String email;
    private final Role role;
}