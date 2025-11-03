package com.j3s.yobuddy.domain.user.dto;

import com.j3s.yobuddy.domain.user.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
	private String phoneNumber;
	private Long departmentId;
	private String currentPassword;
	private String newPassword;
	private Role role;
}
