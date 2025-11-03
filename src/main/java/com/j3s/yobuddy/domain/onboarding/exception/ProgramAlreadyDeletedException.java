package com.j3s.yobuddy.domain.onboarding.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ProgramAlreadyDeletedException extends BusinessException {

    public ProgramAlreadyDeletedException(Long id) {
        super("이미 삭제된 프로그램입니다. (id=" + id + ")", HttpStatus.CONFLICT);
    }
}
