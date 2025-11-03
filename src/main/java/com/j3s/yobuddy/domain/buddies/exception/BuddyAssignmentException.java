package com.j3s.yobuddy.domain.buddies.exception;

import org.springframework.http.HttpStatus;
import com.j3s.yobuddy.common.exception.BusinessException;

public class BuddyAssignmentException extends BusinessException {
    public BuddyAssignmentException(Long userId) {
        super("해당 유저(" + userId + ")는 버디로 지정되어 있지 않습니다.", HttpStatus.FORBIDDEN);
    }
}
