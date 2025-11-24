package com.j3s.yobuddy.api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.j3s.yobuddy.common.security.JwtTokenProvider;
import com.j3s.yobuddy.domain.auth.dto.LoginRequest;
import com.j3s.yobuddy.domain.auth.dto.TokenResponse;
import com.j3s.yobuddy.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest req) {
        TokenResponse resp = authService.login(req);

        return ResponseEntity.ok()
            .header("Authorization", "Bearer " + resp.getAccessToken())
            .header("Refresh-Token", resp.getRefreshToken())
            .header("Access-Token-Expires-In", String.valueOf(resp.getAccessExpiresIn()))
            .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
        @RequestHeader("Refresh-Token") String refreshToken) {

        TokenResponse resp = authService.refresh(refreshToken);

        return ResponseEntity.ok()
            .header("Authorization", "Bearer " + resp.getAccessToken())
            .header("Refresh-Token", resp.getRefreshToken())
            .header("Access-Token-Expires-In", String.valueOf(resp.getAccessExpiresIn()))
            .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @RequestHeader("Authorization") String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String token = authorization.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }
}
