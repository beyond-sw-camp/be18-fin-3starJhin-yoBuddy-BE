package com.j3s.yobuddy.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class RegisterRequest {
    private final String email;
    private final String password;
}
