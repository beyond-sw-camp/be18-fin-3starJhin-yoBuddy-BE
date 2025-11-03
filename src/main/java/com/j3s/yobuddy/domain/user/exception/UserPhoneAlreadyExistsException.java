package com.j3s.yobuddy.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.j3s.yobuddy.common.exception.BusinessException;

public class UserPhoneAlreadyExistsException extends BusinessException {

	public UserPhoneAlreadyExistsException(String phoneNumber) {
		super("이미 사용 중인 연락처입니다. (phoneNumber=" + phoneNumber + ")", HttpStatus.CONFLICT);
	}
}
