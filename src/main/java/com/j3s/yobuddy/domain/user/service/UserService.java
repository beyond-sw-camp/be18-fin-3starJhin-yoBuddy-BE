package com.j3s.yobuddy.domain.user.service;

import com.j3s.yobuddy.domain.user.dto.RegisterRequest;
import com.j3s.yobuddy.domain.user.entity.User;

public interface UserService {
	User register(RegisterRequest req);
}
