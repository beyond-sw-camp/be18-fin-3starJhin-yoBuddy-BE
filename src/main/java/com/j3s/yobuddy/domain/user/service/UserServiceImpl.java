package com.j3s.yobuddy.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.j3s.yobuddy.domain.user.dto.RegisterRequest;
import com.j3s.yobuddy.domain.user.entity.Role;
import com.j3s.yobuddy.domain.user.entity.Users;
import com.j3s.yobuddy.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public Users register(RegisterRequest req) {
		String name = req.getName();
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Name is required");
		}

		String email = req.getEmail();
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Email is required");
		}

		if (req.getPassword() == null || req.getPassword().isBlank()) {
			throw new IllegalArgumentException("Password is required");
		}

		String phoneNumber = req.getPhoneNumber();
		if (phoneNumber == null || phoneNumber.isBlank()) {
			throw new IllegalArgumentException("Phone number is required");
		}

		userRepository.findByEmail(email).ifPresent(u -> {
			throw new IllegalArgumentException("Email already in use");
		});

		userRepository.findByPhoneNumber(phoneNumber).ifPresent(u -> {
			throw new IllegalArgumentException("Phone number already in use");
		});

		String encoded = passwordEncoder.encode(req.getPassword());
		Role role = req.getRole() != null ? req.getRole() : Role.USER;

		Users user = Users.builder()
			.name(name)
			.email(email)
			.password(encoded)
			.phoneNumber(phoneNumber)
			.role(role)
			.joinedAt(req.getJoinedAt())
			.build();

		return userRepository.save(user);
	}
}
