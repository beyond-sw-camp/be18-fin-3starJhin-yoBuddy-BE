package com.j3s.yobuddy.domain.auth.service;

import com.j3s.yobuddy.common.config.JwtProperties;
import com.j3s.yobuddy.common.mail.EmailService;
import com.j3s.yobuddy.common.security.JwtTokenProvider;
import com.j3s.yobuddy.domain.auth.dto.LoginRequest;
import com.j3s.yobuddy.domain.auth.dto.TokenResponse;
import com.j3s.yobuddy.domain.auth.entity.PasswordResetToken;
import com.j3s.yobuddy.domain.auth.repository.PasswordResetTokenRepository;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.front-url}")
    private String frontUrl;

    private String redisKey(Long userId) {
        return "refresh:" + userId;
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmailAndIsDeletedFalse(req.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        Long userId = user.getUserId();

        String access = jwtTokenProvider.createAccessToken(
            userId, user.getEmail(), user.getRole().name());
        String refresh = jwtTokenProvider.createRefreshToken(userId);

        redisTemplate.opsForValue().set(
            redisKey(userId),
            refresh,
            Duration.ofMillis(jwtProperties.getRefreshExpirationMs())
        );

        return new TokenResponse(access, refresh, jwtProperties.getAccessExpirationMs());
    }

    @Override
    @Transactional
    public TokenResponse refresh(String refreshToken) {
        Jws<Claims> claims;
        try {
            claims = jwtTokenProvider.parseClaims(refreshToken);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        Long userId = Long.valueOf(claims.getBody().getSubject());
        String saved = redisTemplate.opsForValue().get(redisKey(userId));

        if (saved == null || !saved.equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token not recognized");
        }

        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newAccess = jwtTokenProvider.createAccessToken(
            userId, user.getEmail(), user.getRole().name());
        String newRefresh = jwtTokenProvider.createRefreshToken(userId);

        redisTemplate.opsForValue().set(
            redisKey(userId),
            newRefresh,
            Duration.ofMillis(jwtProperties.getRefreshExpirationMs())
        );

        return new TokenResponse(newAccess, newRefresh, jwtProperties.getAccessExpirationMs());
    }

    @Override
    @Transactional
    public void logout(Long userId) {
        redisTemplate.delete(redisKey(userId));
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {

        userRepository.findByEmailAndIsDeletedFalse(email)
            .ifPresent(user -> {

                passwordResetTokenRepository.deleteByUser(user);

                String token = UUID.randomUUID().toString();

                PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiredAt(LocalDateTime.now().plusMinutes(30))
                    .used(false)
                    .build();

                passwordResetTokenRepository.save(resetToken);

                String resetLink = frontUrl + "/reset-password?token=" + token;

                String body = """
                    비밀번호 재설정을 요청하셨습니다.<br><br>
                    아래 버튼을 클릭하여 비밀번호를 변경해주세요.<br><br>
                    <a href="%s"
                       style="display:inline-block; padding:12px 18px;
                              background:#294594; color:#fff;
                              text-decoration:none; border-radius:8px;">
                       비밀번호 재설정
                    </a><br><br>
                    본 링크는 30분간 유효합니다.
                    """.formatted(resetLink);

                emailService.send(
                    user.getEmail(),
                    "비밀번호 재설정 안내",
                    body
                );
            });
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("만료된 토큰입니다.");
        }

        User user = resetToken.getUser();

        user.changePassword(passwordEncoder.encode(newPassword));
        resetToken.use();

        redisTemplate.delete(redisKey(user.getUserId()));
    }
}
