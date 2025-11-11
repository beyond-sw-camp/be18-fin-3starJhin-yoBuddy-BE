package com.j3s.yobuddy.domain.mentor.exception;

import org.springframework.http.HttpStatus;
import com.j3s.yobuddy.common.exception.BusinessException;

public class MenteeAlreadyAssignedException extends BusinessException {
    public MenteeAlreadyAssignedException(Long menteeId) {
        super("해당 멘티는 이미 다른 멘토에게 배정되어 있습니다. (menteeId=" + menteeId + ")", HttpStatus.CONFLICT);
    }
}
