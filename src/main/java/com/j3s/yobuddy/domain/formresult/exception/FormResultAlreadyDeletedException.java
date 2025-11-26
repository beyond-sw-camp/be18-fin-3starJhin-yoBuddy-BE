package com.j3s.yobuddy.domain.formresult.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class FormResultAlreadyDeletedException extends BusinessException {

    public FormResultAlreadyDeletedException(Long formResultId) {
        super("이미 삭제된 폼 결과입니다. (id=" + formResultId + ")", HttpStatus.CONFLICT);
    }
}
