package com.j3s.yobuddy.domain.mentor.mentoring.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MenteeNotFoundException extends BusinessException {
    public MenteeNotFoundException(Long userId) {
        super("해당 멘티 정보를 찾을 수 없습니다. (userId=" + userId + ")", HttpStatus.NOT_FOUND);
    }
}
