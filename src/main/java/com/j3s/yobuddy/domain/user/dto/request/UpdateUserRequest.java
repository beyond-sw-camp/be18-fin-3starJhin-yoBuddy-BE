package com.j3s.yobuddy.domain.user.dto.request;

import com.j3s.yobuddy.domain.user.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateUserRequest {
	private final String phoneNumber;
	private final Long departmentId;
	private final String currentPassword;
	private final String newPassword;
	private final Role role;
}
