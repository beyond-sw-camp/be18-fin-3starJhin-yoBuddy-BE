package com.j3s.yobuddy.domain.mentor.exception;

import org.springframework.http.HttpStatus;
import com.j3s.yobuddy.common.exception.BusinessException;

public class AssignmentNotFoundException extends BusinessException {
    public AssignmentNotFoundException(Long menteeId) {
        super("해당 멘티에 대한 멘토링 관계를 찾을 수 없습니다. (menteeId=" + menteeId + ")", HttpStatus.NOT_FOUND);
    }
}
