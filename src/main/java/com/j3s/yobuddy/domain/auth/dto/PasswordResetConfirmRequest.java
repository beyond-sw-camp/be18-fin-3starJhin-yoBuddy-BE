package com.j3s.yobuddy.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetConfirmRequest {
    private String token;
    private String newPassword;
}
