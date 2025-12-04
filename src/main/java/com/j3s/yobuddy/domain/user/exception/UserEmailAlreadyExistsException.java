package com.j3s.yobuddy.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.j3s.yobuddy.common.exception.BusinessException;

public class UserEmailAlreadyExistsException extends BusinessException {

	public UserEmailAlreadyExistsException(String email) {
		super("이미 사용 중인 이메일입니다. (email=" + email + ")", HttpStatus.CONFLICT);
	}
}
