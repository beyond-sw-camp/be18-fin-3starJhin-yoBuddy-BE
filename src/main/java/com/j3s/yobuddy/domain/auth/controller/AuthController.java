package com.j3s.yobuddy.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.j3s.yobuddy.common.security.JwtTokenProvider;
import com.j3s.yobuddy.domain.auth.dto.LoginRequest;
import com.j3s.yobuddy.domain.auth.dto.RefreshRequest;
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
	public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
		TokenResponse resp = authService.login(req);
		return ResponseEntity.ok(resp);
	}

	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest body) {
		TokenResponse resp = authService.refresh(body.getRefreshToken());
		return ResponseEntity.ok(resp);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorization) {
		// expect: Authorization: Bearer <access-token>
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
