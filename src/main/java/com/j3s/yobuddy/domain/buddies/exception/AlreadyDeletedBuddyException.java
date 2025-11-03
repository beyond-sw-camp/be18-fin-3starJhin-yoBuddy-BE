package com.j3s.yobuddy.domain.buddies.exception;

import org.springframework.http.HttpStatus;
import com.j3s.yobuddy.common.exception.BusinessException;

public class AlreadyDeletedBuddyException extends BusinessException {
    public AlreadyDeletedBuddyException(Long buddyId) {
        super("이미 삭제된 버디입니다. (buddyId=" + buddyId + ")", HttpStatus.CONFLICT);
    }
}
