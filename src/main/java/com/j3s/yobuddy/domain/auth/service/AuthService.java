package com.j3s.yobuddy.domain.auth.service;

import com.j3s.yobuddy.domain.auth.dto.LoginRequest;
import com.j3s.yobuddy.domain.auth.dto.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest req);
    TokenResponse refresh(String refreshToken);
    void logout(Long userId);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
}
