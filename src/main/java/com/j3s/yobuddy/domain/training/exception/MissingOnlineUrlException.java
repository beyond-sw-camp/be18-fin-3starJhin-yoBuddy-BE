package com.j3s.yobuddy.domain.training.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MissingOnlineUrlException extends BusinessException {

    public MissingOnlineUrlException() {
        super("온라인 교육은 URL 입력이 필수입니다.", HttpStatus.BAD_REQUEST);
    }
}
