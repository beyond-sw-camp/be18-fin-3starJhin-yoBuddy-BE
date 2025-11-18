package com.j3s.yobuddy.domain.programenrollment.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ProgramEnrollmentNotFoundException extends BusinessException {
    public ProgramEnrollmentNotFoundException(Long userId) {
        super("해당 사용자에 대한 활성화된 온보딩 프로그램을 찾을 수 없습니다. (userId=" + userId + ")", HttpStatus.NOT_FOUND);
    }
}
