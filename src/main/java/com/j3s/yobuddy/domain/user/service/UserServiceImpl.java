package com.j3s.yobuddy.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.user.dto.RegisterRequest;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.User;
import com.j3s.yobuddy.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public User register(RegisterRequest req) {
		String email = req.getEmail();
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Email is required");
		}
		if (req.getPassword() == null || req.getPassword().isBlank()) {
			throw new IllegalArgumentException("Password is required");
		}

		userRepository.findByEmail(email).ifPresent(u -> {
			throw new IllegalArgumentException("Email already in use");
		});

		String encoded = passwordEncoder.encode(req.getPassword());

		User user = User.builder()
			.email(email)
			.password(encoded)
			.role(Role.USER)
			.enabled(true)
			.build();

		return userRepository.save(user);
	}
}
