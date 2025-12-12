package com.j3s.yobuddy.domain.auth.repository;

import com.j3s.yobuddy.domain.auth.entity.PasswordResetToken;
import com.j3s.yobuddy.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository
    extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}
