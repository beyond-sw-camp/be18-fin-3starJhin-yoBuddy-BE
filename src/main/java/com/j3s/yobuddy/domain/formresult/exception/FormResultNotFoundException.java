package com.j3s.yobuddy.domain.formresult.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class FormResultNotFoundException extends BusinessException {

    public FormResultNotFoundException(Long formResultId) {
        super("존재하지 않거나 삭제된 폼 결과입니다. (id=" + formResultId + ")", HttpStatus.NOT_FOUND);
    }
}
