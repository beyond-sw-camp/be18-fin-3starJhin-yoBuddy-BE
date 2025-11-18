package com.j3s.yobuddy.domain.training.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ProgramAlreadyCompletedException extends BusinessException {

    public ProgramAlreadyCompletedException(Long programId) {
        super(
            "이미 완료된 프로그램입니다. 교육을 해제할 수 없습니다. (programId=" + programId + ")",
            HttpStatus.CONFLICT
        );
    }
}
