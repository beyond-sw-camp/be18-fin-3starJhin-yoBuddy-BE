package com.j3s.yobuddy.domain.programenrollment.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EnrollmentNotFoundException extends BusinessException {
    public EnrollmentNotFoundException(Long id) {
        super("해당 참여 내역을 찾을 수 없습니다. (id=" + id + ")", HttpStatus.NOT_FOUND);
    }
}
