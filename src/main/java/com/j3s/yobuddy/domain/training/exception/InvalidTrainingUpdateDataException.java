package com.j3s.yobuddy.domain.training.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidTrainingUpdateDataException extends BusinessException {

    public InvalidTrainingUpdateDataException() {
        super("수정할 교육 데이터가 없습니다. 최소 한 개 이상의 필드를 입력해야 합니다.", HttpStatus.BAD_REQUEST);
    }
}
