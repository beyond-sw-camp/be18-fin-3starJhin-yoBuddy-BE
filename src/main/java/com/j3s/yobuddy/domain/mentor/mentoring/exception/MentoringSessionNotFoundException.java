package com.j3s.yobuddy.domain.mentor.mentoring.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MentoringSessionNotFoundException extends BusinessException {
    public MentoringSessionNotFoundException(Long sessionId) {
        super("멘토링 세션을 찾을 수 없습니다. (sessionId=" + sessionId + ")", HttpStatus.NOT_FOUND);
    }
}