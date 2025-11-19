package com.j3s.yobuddy.domain.training.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ForbiddenOperationException extends BusinessException {

    public ForbiddenOperationException(Long userId) {
        super(
            "사용자의 교육 정보를 조회할 권한이 없습니다. (userId=" + userId + ")",
            HttpStatus.FORBIDDEN
        );
    }
}
