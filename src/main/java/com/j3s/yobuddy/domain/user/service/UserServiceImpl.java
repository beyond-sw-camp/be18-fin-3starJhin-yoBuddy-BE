package com.j3s.yobuddy.domain.user.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
	public List<Users> register(List<RegisterRequest> reqs) {
		if (reqs == null || reqs.isEmpty()) {
			throw new IllegalArgumentException("At least one user is required");
		}

		Set<String> emailsInBatch = new HashSet<>();
		Set<String> phonesInBatch = new HashSet<>();
		List<Users> toSave = new ArrayList<>(reqs.size());

		for (RegisterRequest req : reqs) {
			Users user = buildUser(req);

			if (!emailsInBatch.add(user.getEmail())) {
				throw new IllegalArgumentException("Duplicate email in batch: " + user.getEmail());
			}
			if (!phonesInBatch.add(user.getPhoneNumber())) {
				throw new IllegalArgumentException("Duplicate phone number in batch: " + user.getPhoneNumber());
			}

			userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
				throw new IllegalArgumentException("Email already in use: " + user.getEmail());
			});
			userRepository.findByPhoneNumber(user.getPhoneNumber()).ifPresent(u -> {
				throw new IllegalArgumentException("Phone number already in use: " + user.getPhoneNumber());
			});

			toSave.add(user);
		}

		return userRepository.saveAll(toSave);
	}

	private Users buildUser(RegisterRequest req) {
		String name = req.getName();
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Name is required");
		}

		String email = req.getEmail();
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("Email is required");
		}

		String password = req.getPassword();
		if (password == null || password.isBlank()) {
			throw new IllegalArgumentException("Password is required");
		}

		String phoneNumber = req.getPhoneNumber();
		if (phoneNumber == null || phoneNumber.isBlank()) {
			throw new IllegalArgumentException("Phone number is required");
		}

		Role role = Objects.requireNonNullElse(req.getRole(), Role.USER);
		String encoded = passwordEncoder.encode(password);

		return Users.builder()
			.name(name)
			.email(email)
			.password(encoded)
			.phoneNumber(phoneNumber)
			.role(role)
			.joinedAt(req.getJoinedAt())
			.build();
	}
}
