package com.j3s.yobuddy.domain.programenrollment.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateEnrollmentException extends BusinessException {
    public DuplicateEnrollmentException(Long userId, Long programId) {
        super("이미 등록된 사용자입니다. (userId=" + userId + ", programId=" + programId + ")", HttpStatus.CONFLICT);
    }
}