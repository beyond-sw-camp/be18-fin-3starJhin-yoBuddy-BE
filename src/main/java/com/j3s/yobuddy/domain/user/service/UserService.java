package com.j3s.yobuddy.domain.user.service;

import com.j3s.yobuddy.domain.user.dto.RegisterRequest;
import com.j3s.yobuddy.domain.user.entity.User;

public interface UserService {
	/**
	 * Register a new user. Throws IllegalArgumentException if email already exists.
	 * @param req registration data
	 * @return saved User
	 */
	User register(RegisterRequest req);
}
