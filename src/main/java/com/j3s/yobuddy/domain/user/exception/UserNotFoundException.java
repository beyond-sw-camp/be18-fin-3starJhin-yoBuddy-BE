package com.j3s.yobuddy.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.j3s.yobuddy.common.exception.BusinessException;

public class UserNotFoundException extends BusinessException {

	public UserNotFoundException(Long userId) {
		super("존재하지 않거나 삭제된 사용자입니다. (id=" + userId + ")", HttpStatus.NOT_FOUND);
	}
}
