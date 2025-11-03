package com.j3s.yobuddy.domain.buddies.exception;

import org.springframework.http.HttpStatus;
import com.j3s.yobuddy.common.exception.BusinessException;

public class BuddyNotFoundException extends BusinessException {
    public BuddyNotFoundException(Long buddyId) {
        super("해당 버디를 찾을 수 없습니다. buddyId=" + buddyId, HttpStatus.NOT_FOUND);
    }
}
