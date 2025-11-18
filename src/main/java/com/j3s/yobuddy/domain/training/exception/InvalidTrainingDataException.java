package com.j3s.yobuddy.domain.training.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidTrainingDataException extends BusinessException {

    public InvalidTrainingDataException(String detail) {
        super("유효하지 않은 교육 데이터입니다. " + detail, HttpStatus.BAD_REQUEST);
    }
}
