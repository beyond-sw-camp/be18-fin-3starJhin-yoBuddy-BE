package com.j3s.yobuddy.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.j3s.yobuddy.common.exception.BusinessException;

public class InvalidUserRequestException extends BusinessException {

	public InvalidUserRequestException(String message) {
		super(message, HttpStatus.BAD_REQUEST);
	}
}
