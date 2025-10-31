package com.j3s.yobuddy.domain.auth.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.common.config.JwtProperties;
import com.j3s.yobuddy.common.security.JwtTokenProvider;
import com.j3s.yobuddy.domain.auth.dto.LoginRequest;
import com.j3s.yobuddy.domain.auth.dto.TokenResponse;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    private String redisKeyFor(Long userId) {
        return "refresh:" + userId;
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String access = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refresh = jwtTokenProvider.createRefreshToken(user.getId());

        // store refresh in redis with expiration
        long ttlMs = jwtProperties.getRefreshExpirationMs();
        redisTemplate.opsForValue().set(redisKeyFor(user.getId()), refresh, Duration.ofMillis(ttlMs));

        return new TokenResponse(access, refresh, jwtProperties.getAccessExpirationMs());
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validate(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Jws<Claims> claims = jwtTokenProvider.parseClaims(refreshToken);
        Long userId = Long.valueOf(claims.getBody().getSubject());

        String key = redisKeyFor(userId);
        String saved = redisTemplate.opsForValue().get(key);
        if (saved == null || !saved.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token not recognized");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // issue new tokens (rotate refresh token)
        String newAccess = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String newRefresh = jwtTokenProvider.createRefreshToken(user.getId());

        redisTemplate.opsForValue().set(key, newRefresh, Duration.ofMillis(jwtProperties.getRefreshExpirationMs()));

        return new TokenResponse(newAccess, newRefresh, jwtProperties.getAccessExpirationMs());
    }

    @Override
    @Transactional
    public void logout(Long userId) {
        redisTemplate.delete(redisKeyFor(userId));
    }
}
