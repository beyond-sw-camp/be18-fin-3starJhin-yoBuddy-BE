package com.j3s.yobuddy.domain.onboarding.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ProgramStatusChangeNotAllowedException extends BusinessException {

    public ProgramStatusChangeNotAllowedException(Long programId) {
        super(
            "완료된 프로그램은 상태를 변경할 수 없습니다. (id=" + programId + ")",
            HttpStatus.CONFLICT
        );
    }
}