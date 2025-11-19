package com.j3s.yobuddy.domain.training.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserTrainingsNotFoundException extends BusinessException {

    public UserTrainingsNotFoundException(Long userId) {
        super(
            "해당 사용자의 교육 정보를 찾을 수 없습니다. (userId=" + userId + ")",
            HttpStatus.NOT_FOUND
        );
    }
}
