package com.j3s.yobuddy.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.j3s.yobuddy.common.exception.BusinessException;

public class UserPasswordMismatchException extends BusinessException {

	public UserPasswordMismatchException() {
		super("현재 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
	}
}
