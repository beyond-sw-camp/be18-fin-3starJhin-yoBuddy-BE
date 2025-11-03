package com.j3s.yobuddy.domain.buddies.exception;

import com.j3s.yobuddy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class BuddyAlreadyAssignedException extends BusinessException {
    public BuddyAlreadyAssignedException(Long userId) {
        super("해당 유저(" + userId + ")는 이미 버디로 지정되어 있습니다.", HttpStatus.CONFLICT);
    }
}