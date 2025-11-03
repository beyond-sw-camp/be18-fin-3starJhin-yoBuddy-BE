package com.j3s.yobuddy.domain.user.service;

import java.util.List;

import com.j3s.yobuddy.domain.user.dto.RegisterRequest;
import com.j3s.yobuddy.domain.user.entity.Users;

public interface UserService {
	List<Users> register(List<RegisterRequest> reqs);
}
