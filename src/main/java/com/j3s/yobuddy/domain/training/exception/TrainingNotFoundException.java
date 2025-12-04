package com.j3s.yobuddy.domain.training.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class TrainingNotFoundException extends BusinessException {

    public TrainingNotFoundException(Long trainingId) {
        super("존재하지 않는 교육입니다. (trainingId=" + trainingId + ")", HttpStatus.NOT_FOUND);
    }

    public TrainingNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
