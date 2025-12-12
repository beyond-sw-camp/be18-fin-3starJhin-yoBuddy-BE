package com.j3s.yobuddy.api.auth;

import com.j3s.yobuddy.domain.auth.dto.LoginRequest;
import com.j3s.yobuddy.domain.auth.dto.PasswordResetConfirmRequest;
import com.j3s.yobuddy.domain.auth.dto.PasswordResetRequest;
import com.j3s.yobuddy.domain.auth.dto.TokenResponse;
import com.j3s.yobuddy.domain.auth.service.AuthService;
import com.j3s.yobuddy.domain.notification.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SseEmitterManager sseEmitterManager;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.same-site}")
    private String cookieSameSite;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest req) {
        TokenResponse resp = authService.login(req);

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", resp.getAccessToken())
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSameSite)
            .path("/")
            .maxAge(resp.getAccessExpiresIn() / 1000)
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", resp.getRefreshToken())
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSameSite)
            .path("/")
            .maxAge(60 * 60 * 24 * 14)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("REFRESH_TOKEN") String refreshToken) {
        TokenResponse resp = authService.refresh(refreshToken);

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", resp.getAccessToken())
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSameSite)
            .path("/")
            .maxAge(resp.getAccessExpiresIn() / 1000)
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", resp.getRefreshToken())
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSameSite)
            .path("/")
            .maxAge(60 * 60 * 24 * 14)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body("refresh success");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal String userId) {

        authService.logout(Long.valueOf(userId));
        sseEmitterManager.disconnect(Long.valueOf(userId));

        ResponseCookie clearAccess = ResponseCookie.from("ACCESS_TOKEN", "")
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSameSite)
            .path("/")
            .maxAge(0)
            .build();

        ResponseCookie clearRefresh = ResponseCookie.from("REFRESH_TOKEN", "")
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSameSite)
            .path("/")
            .maxAge(0)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
            .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
            .body("logout success");
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<Void> requestReset(
        @RequestBody PasswordResetRequest req
    ) {
        authService.requestPasswordReset(req.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> reset(
        @RequestBody PasswordResetConfirmRequest req
    ) {
        authService.resetPassword(req.getToken(), req.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
