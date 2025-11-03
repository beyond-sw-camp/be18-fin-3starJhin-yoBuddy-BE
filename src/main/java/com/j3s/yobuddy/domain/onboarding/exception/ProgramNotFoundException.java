package com.j3s.yobuddy.domain.onboarding.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ProgramNotFoundException extends BusinessException {
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public ProgramNotFoundException(Long id) {
        super("존재하지 않거나 삭제된 프로그램입니다. (id=" + id + ")", HttpStatus.NOT_FOUND);
    }
}

