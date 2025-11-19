package com.j3s.yobuddy.domain.user.dto.request;

import com.j3s.yobuddy.domain.user.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSearchRequest {
    private final String name;
    private final String email;
    private final Role role;
}